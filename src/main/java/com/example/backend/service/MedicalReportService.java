package com.example.backend.service;

import com.example.backend.dto.MedicalReportDTO;
import com.example.backend.entity.Appointment;
import com.example.backend.entity.MedicalReport;
import com.example.backend.entity.User;
import com.example.backend.repository.AppointmentRepository;
import com.example.backend.repository.MedicalReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MedicalReportService {

    private final MedicalReportRepository reportRepo;
    private final AppointmentRepository appointmentRepo;

    public MedicalReport createIfNotExists(Long appointmentId, User doctor) {

        Appointment appt = appointmentRepo.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        if (!appt.getDoctor().getId().equals(doctor.getId())) {
            throw new SecurityException("Not your appointment");
        }

        return reportRepo.findByAppointmentId(appointmentId)
                .orElseGet(() ->
                        reportRepo.save(
                                MedicalReport.builder()
                                        .appointment(appt)
                                        .build()
                        )
                );
    }

    public MedicalReport updateReport(
            Long appointmentId,
            MedicalReportDTO dto,
            User doctor) {

        MedicalReport report = createIfNotExists(appointmentId, doctor);

        report.setSymptoms(dto.getSymptoms());
        report.setDiagnosis(dto.getDiagnosis());
        report.setPrescription(dto.getPrescription());
        report.setAdvice(dto.getAdvice());

        return reportRepo.save(report);
    }

    public List<MedicalReport> getPatientHistory(Long patientId) {
        return reportRepo
                .findByAppointment_Patient_IdOrderByCreatedAtDesc(patientId);
    }

    public MedicalReport getForPatient(Long appointmentId, User patient) {

        MedicalReport report = reportRepo.findByAppointmentId(appointmentId)
                .orElseThrow(() -> new RuntimeException("Report not found"));

        if (!report.getAppointment().getPatient().getId().equals(patient.getId())) {
            throw new SecurityException("Not your report");
        }

        return report;
    }
}
