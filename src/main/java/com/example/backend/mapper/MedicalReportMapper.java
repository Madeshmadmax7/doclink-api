package com.example.backend.mapper;
import com.example.backend.dto.MedicalReportDTO;
import com.example.backend.entity.MedicalReport;
import org.springframework.stereotype.Component;

@Component
public class MedicalReportMapper {

    public MedicalReportDTO toDto(MedicalReport r) {
        MedicalReportDTO dto = new MedicalReportDTO();
        dto.setId(r.getId());
        dto.setAppointmentId(r.getAppointment().getId());
        dto.setSymptoms(r.getSymptoms());
        dto.setDiagnosis(r.getDiagnosis());
        dto.setPrescription(r.getPrescription());
        dto.setAdvice(r.getAdvice());
        dto.setCreatedAt(r.getCreatedAt());
        dto.setUpdatedAt(r.getUpdatedAt());
        return dto;
    }

    public void updateEntity(MedicalReport r, MedicalReportDTO dto) {
        r.setSymptoms(dto.getSymptoms());
        r.setDiagnosis(dto.getDiagnosis());
        r.setPrescription(dto.getPrescription());
        r.setAdvice(dto.getAdvice());
    }
}
