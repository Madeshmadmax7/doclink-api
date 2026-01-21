package com.example.backend.controller;

import com.example.backend.dto.AppointmentDTO;
import com.example.backend.entity.User;
import com.example.backend.mapper.AppointmentMapper;
import com.example.backend.service.AppointmentService;
import com.example.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

        private final AppointmentService appointmentService;
        private final UserService userService;
        private final AppointmentMapper appointmentMapper;

        @PreAuthorize("hasRole('PATIENT')")
        @PostMapping("/request")
        public AppointmentDTO request(
                        @RequestParam Long doctorId,
                        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime requestedTime,
                        Authentication authentication) {

                User patient = userService.getByEmail(authentication.getName());
                return appointmentMapper.toDto(
                                appointmentService.request(patient, doctorId, requestedTime));
        }

        // ================= NURSE VIEW =================
        @PreAuthorize("hasRole('NURSE')")
        @GetMapping("/pending")
        public List<AppointmentDTO> pendingRequests() {
                return appointmentService.getPendingRequests()
                                .stream()
                                .map(appointmentMapper::toDto)
                                .toList();
        }

        // ================= NURSE APPROVE =================
        // Nurse assigns ONLY time & duration
        @PreAuthorize("hasRole('NURSE')")
        @PostMapping("/{appointmentId}/approve")
        public AppointmentDTO approve(
                        @PathVariable Long appointmentId,
                        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
                        @RequestParam Integer durationMinutes,
                        Authentication authentication) {
                User nurse = userService.getByEmail(authentication.getName());

                return appointmentMapper.toDto(
                                appointmentService.approve(
                                                appointmentId,
                                                nurse,
                                                startTime,
                                                durationMinutes));
        }

        // ================= DOCTOR VIEWS =================
        @PreAuthorize("hasRole('DOCTOR')")
        @GetMapping("/doctor")
        public List<AppointmentDTO> doctorAppointments(Authentication authentication) {
                User doctor = userService.getByEmail(authentication.getName());

                return appointmentService.getDoctorAppointments(doctor.getId())
                                .stream()
                                .map(appointmentMapper::toDto)
                                .toList();
        }

        @PreAuthorize("hasRole('DOCTOR')")
        @GetMapping("/doctor/today")
        public List<AppointmentDTO> todayAppointments(Authentication authentication) {
                User doctor = userService.getByEmail(authentication.getName());

                return appointmentService.getTodayAppointments(doctor.getId())
                                .stream()
                                .map(appointmentMapper::toDto)
                                .toList();
        }

        // ================= PATIENT VIEW =================
        @PreAuthorize("hasRole('PATIENT')")
        @GetMapping("/patient")
        public List<AppointmentDTO> patientAppointments(Authentication authentication) {
                User patient = userService.getByEmail(authentication.getName());

                return appointmentService.getPatientAppointments(patient.getId())
                                .stream()
                                .map(appointmentMapper::toDto)
                                .toList();
        }

        // ================= RESCHEDULE =================

        @PreAuthorize("hasRole('NURSE')")
        @GetMapping("/availability")
        public boolean checkAvailability(
                        @RequestParam Long doctorId,
                        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
                        @RequestParam Integer durationMinutes) {
                return appointmentService.isSlotAvailable(
                                doctorId,
                                startTime,
                                durationMinutes);
        }

        @PreAuthorize("hasAnyRole('NURSE','DOCTOR')")
        @PutMapping("/{appointmentId}/reschedule")
        public AppointmentDTO reschedule(
                        @PathVariable Long appointmentId,
                        @RequestParam Integer durationMinutes,
                        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime newTime) {
                return appointmentMapper.toDto(
                                appointmentService.reschedule(
                                                appointmentId,
                                                newTime,
                                                durationMinutes));
        }

        @PreAuthorize("hasRole('PATIENT')")
        @PostMapping("/{appointmentId}/request-reschedule")
        public void requestReschedule(
                        @PathVariable Long appointmentId,
                        Authentication auth) {
                User patient = userService.getByEmail(auth.getName());
                appointmentService.patientRequestReschedule(appointmentId, patient);
        }

        @PreAuthorize("hasRole('PATIENT')")
        @PostMapping("/{appointmentId}/cancel")
        public void cancel(
                        @PathVariable Long appointmentId,
                        Authentication auth) {
                User patient = userService.getByEmail(auth.getName());
                appointmentService.patientCancel(appointmentId, patient);
        }

        @PreAuthorize("hasRole('NURSE')")
        @PostMapping("/{appointmentId}/doctor-leave")
        public void doctorLeave(
                        @PathVariable Long appointmentId,
                        Authentication auth) {

                User nurse = userService.getByEmail(auth.getName());
                appointmentService.nurseMarkDoctorLeave(appointmentId, nurse);
        }

        @PreAuthorize("hasRole('NURSE')")
        @PostMapping("/{appointmentId}/cancel-by-nurse")
        public void cancelByNurse(
                        @PathVariable Long appointmentId,
                        Authentication auth) {

                User nurse = userService.getByEmail(auth.getName());
                appointmentService.nurseCancel(appointmentId, nurse);
        }

}
