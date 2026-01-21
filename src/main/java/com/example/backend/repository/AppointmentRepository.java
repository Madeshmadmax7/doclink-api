package com.example.backend.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backend.entity.Appointment;
import com.example.backend.entity.AppointmentStatus;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

        List<Appointment> findByDoctorId(Long doctorId);

        List<Appointment> findByPatientId(Long patientId);

        List<Appointment> findByNurseId(Long nurseId);

        List<Appointment> findByStatus(AppointmentStatus status);

        List<Appointment> findByDoctorIdAndAppointmentTimeBetween(
                        Long doctorId,
                        LocalDateTime start,
                        LocalDateTime end);

        List<Appointment> findByDoctorIdAndAppointmentTimeAfterOrderByAppointmentTime(
                        Long doctorId,
                        LocalDateTime time);

        List<Appointment> findByDoctorIdAndStatusAndAppointmentTimeBetween(
                        Long doctorId,
                        AppointmentStatus status,
                        LocalDateTime start,
                        LocalDateTime end);

}
