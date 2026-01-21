package com.example.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backend.entity.MedicalReport;

public interface MedicalReportRepository extends JpaRepository<MedicalReport, Long> {

    Optional<MedicalReport> findByAppointmentId(Long appointmentId);

    List<MedicalReport> findByAppointment_Patient_IdOrderByCreatedAtDesc(Long patientId);
}
