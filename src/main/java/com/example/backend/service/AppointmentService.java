package com.example.backend.service;

import com.example.backend.entity.*;
import com.example.backend.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentService {

        private static final int BUFFER_MINUTES = 10;

        private final AppointmentRepository appointmentRepository;
        private final UserService userService;
        private final DoctorService doctorService;
        private final DoctorLeaveService doctorLeaveService;
        private final NotificationService notificationService;

        public Appointment request(User patient, Long doctorId, LocalDateTime requestedTime) {

                User doctorUser = userService.getById(doctorId);

                Appointment appointment = Appointment.builder()
                                .patient(patient)
                                .doctor(doctorUser)
                                .requestedTime(requestedTime)
                                .status(AppointmentStatus.REQUESTED)
                                .durationMinutes(10)
                                .build();

                return appointmentRepository.save(appointment);
        }

        /* ================= APPROVE ================= */
        @Transactional
        public Appointment approve(
                        Long appointmentId,
                        User nurse,
                        LocalDateTime startTime,
                        Integer durationMinutes) {

                Appointment appointment = appointmentRepository.findById(appointmentId)
                                .orElseThrow(() -> new IllegalStateException("Appointment not found"));

                if (appointment.getStatus() != AppointmentStatus.REQUESTED) {
                        throw new IllegalStateException("Appointment not in REQUESTED state");
                }

                User doctorUser = appointment.getDoctor();
                Doctor doctor = doctorService.getOrCreateDoctor(doctorUser);

                if (doctorLeaveService.isDoctorOnLeave(doctor, startTime.toLocalDate())) {
                        notificationService.notify(
                                        appointment.getPatient(),
                                        "Doctor is unavailable on " + startTime.toLocalDate());

                        notificationService.notify(
                                        nurse,
                                        "Approval blocked. Doctor is on leave");

                        throw new IllegalStateException("Doctor is on leave");
                }

                appointment.setNurse(nurse);
                appointment.setAppointmentTime(startTime);
                appointment.setDurationMinutes(durationMinutes);
                appointment.setStatus(AppointmentStatus.APPROVED);

                Appointment saved = appointmentRepository.save(appointment);

                shiftFutureAppointments(
                                doctor,
                                saved.getAppointmentTime(),
                                saved.getDurationMinutes());

                notificationService.notify(
                                saved.getPatient(),
                                "Your appointment is approved for " + saved.getAppointmentTime());

                notificationService.notify(
                                doctorUser,
                                "New appointment at " + saved.getAppointmentTime());

                return saved;
        }

        /* ================= RESCHEDULE ================= */
        @Transactional
        public Appointment reschedule(
                        Long appointmentId,
                        LocalDateTime newStartTime,
                        Integer newDurationMinutes) {

                Appointment appointment = appointmentRepository.findById(appointmentId)
                                .orElseThrow(() -> new IllegalStateException("Appointment not found"));

                if (appointment.getStatus() != AppointmentStatus.APPROVED &&
                                appointment.getStatus() != AppointmentStatus.NEEDS_RESCHEDULE) {
                        throw new IllegalStateException("Appointment cannot be rescheduled");
                }

                Doctor doctor = doctorService.getOrCreateDoctor(appointment.getDoctor());

                validateDoctorAvailability(doctor, newStartTime.toLocalDate());

                appointment.setAppointmentTime(newStartTime);
                appointment.setDurationMinutes(newDurationMinutes);
                appointment.setStatus(AppointmentStatus.APPROVED);

                Appointment saved = appointmentRepository.save(appointment);

                shiftFutureAppointments(doctor, newStartTime, newDurationMinutes);

                notificationService.notify(
                                saved.getPatient(),
                                "Appointment rescheduled to " + newStartTime);

                notificationService.notify(
                                appointment.getDoctor(),
                                "Appointment rescheduled to " + newStartTime);

                return saved;
        }

        /* ================= AVAILABILITY ================= */
        public boolean isSlotAvailable(
                        Long doctorId,
                        LocalDateTime startTime,
                        int durationMinutes) {

                Doctor doctor = doctorService.getOrCreateDoctor(
                                userService.getById(doctorId));

                if (doctorLeaveService.isDoctorOnLeave(
                                doctor, startTime.toLocalDate())) {
                        return false;
                }

                LocalDateTime endTime = startTime.plusMinutes(durationMinutes);

                LocalDateTime dayStart = startTime.toLocalDate().atStartOfDay();
                LocalDateTime dayEnd = startTime.toLocalDate().atTime(23, 59, 59);

                List<Appointment> sameDayAppointments = appointmentRepository
                                .findByDoctorIdAndStatusAndAppointmentTimeBetween(
                                                doctorId,
                                                AppointmentStatus.APPROVED,
                                                dayStart,
                                                dayEnd);

                for (Appointment a : sameDayAppointments) {
                        LocalDateTime s = a.getAppointmentTime();
                        LocalDateTime e = s.plusMinutes(a.getDurationMinutes());

                        if (startTime.isBefore(e) && endTime.isAfter(s)) {
                                return false;
                        }
                }
                return true;
        }

        /* ================= SHIFT ================= */
        private void shiftFutureAppointments(
                        Doctor doctor,
                        LocalDateTime fromTime,
                        int durationMinutes) {

                List<Appointment> future = appointmentRepository
                                .findByDoctorIdAndAppointmentTimeAfterOrderByAppointmentTime(
                                                doctor.getId(), fromTime);

                LocalDateTime nextAvailable = fromTime.plusMinutes(durationMinutes + BUFFER_MINUTES);

                for (Appointment appt : future) {

                        while (doctorLeaveService.isDoctorOnLeave(
                                        doctor, nextAvailable.toLocalDate())) {

                                nextAvailable = nextAvailable.toLocalDate()
                                                .plusDays(1)
                                                .atStartOfDay();
                        }

                        appt.setAppointmentTime(nextAvailable);

                        nextAvailable = nextAvailable.plusMinutes(
                                        appt.getDurationMinutes() + BUFFER_MINUTES);
                }

                appointmentRepository.saveAll(future);
        }

        /* ================= FETCH ================= */
        @Transactional
        public List<Appointment> getPatientAppointments(Long patientId) {

                List<Appointment> list = appointmentRepository.findByPatientId(patientId);

                boolean updated = false;

                for (Appointment a : list) {
                        if (autoCompleteIfOver(a)) {
                                updated = true;
                        }
                }

                if (updated) {
                        appointmentRepository.saveAll(list);
                }

                return list;
        }

        public List<Appointment> getDoctorAppointments(Long doctorId) {
                return appointmentRepository.findByDoctorId(doctorId);
        }

        public List<Appointment> getPendingRequests() {
                return appointmentRepository.findByStatus(AppointmentStatus.REQUESTED);
        }

        public List<Appointment> getTodayAppointments(Long doctorId) {
                LocalDateTime start = LocalDate.now().atStartOfDay();
                LocalDateTime end = LocalDate.now().atTime(23, 59, 59);

                return appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(
                                doctorId, start, end);
        }

        /* ================= AUTO COMPLETE ================= */
        private boolean autoCompleteIfOver(Appointment a) {

                if (a.getStatus() != AppointmentStatus.APPROVED)
                        return false;
                if (a.getAppointmentTime() == null)
                        return false;

                LocalDateTime endTime = a.getAppointmentTime().plusMinutes(a.getDurationMinutes());

                if (LocalDateTime.now().isAfter(endTime)) {
                        a.setStatus(AppointmentStatus.COMPLETED);
                        return true;
                }
                return false;
        }

        /* ================= PATIENT ================= */
        @Transactional
        public void patientRequestReschedule(Long appointmentId, User patient) {

                Appointment a = appointmentRepository.findById(appointmentId)
                                .orElseThrow(() -> new IllegalStateException("Appointment not found"));

                if (!a.getPatient().getId().equals(patient.getId())) {
                        throw new SecurityException("Not your appointment");
                }

                if (a.getStatus() == AppointmentStatus.COMPLETED) {
                        throw new IllegalStateException("Appointment already completed");
                }

                a.setStatus(AppointmentStatus.NEEDS_RESCHEDULE);
        }

        @Transactional
        public void patientCancel(Long appointmentId, User patient) {

                Appointment a = appointmentRepository.findById(appointmentId)
                                .orElseThrow(() -> new IllegalStateException("Appointment not found"));

                if (!a.getPatient().getId().equals(patient.getId())) {
                        throw new SecurityException("Not your appointment");
                }

                if (a.getStatus() == AppointmentStatus.COMPLETED) {
                        throw new IllegalStateException("Appointment already completed");
                }

                a.setStatus(AppointmentStatus.CANCELLED);
        }

        private void validateDoctorAvailability(Doctor doctor, LocalDate date) {
                if (doctorLeaveService.isDoctorOnLeave(doctor, date)) {
                        throw new IllegalStateException("Doctor is on leave on " + date);
                }
        }

        @Transactional
        public void nurseMarkDoctorLeave(Long appointmentId, User nurse) {
                Appointment a = appointmentRepository.findById(appointmentId)
                                .orElseThrow(() -> new IllegalStateException("Appointment not found"));
                appointmentRepository.delete(a);
        }

        @Transactional
        public void nurseCancel(Long appointmentId, User nurse) {
                Appointment a = appointmentRepository.findById(appointmentId)
                                .orElseThrow(() -> new IllegalStateException("Appointment not found"));
                appointmentRepository.delete(a);
        }
}
