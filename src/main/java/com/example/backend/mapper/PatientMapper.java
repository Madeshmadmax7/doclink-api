package com.example.backend.mapper;

import com.example.backend.dto.PatientDTO;
import com.example.backend.entity.Patient;
import org.springframework.stereotype.Component;

@Component
public class PatientMapper {

    public PatientDTO toDto(Patient patient) {
        if (patient == null) return null;

        PatientDTO dto = new PatientDTO();
        dto.setId(patient.getId());
        dto.setName(patient.getUser().getName());
        dto.setEmail(patient.getUser().getEmail());
        dto.setGender(patient.getGender());
        dto.setAge(patient.getAge());
        dto.setBloodGroup(patient.getBloodGroup());
        dto.setPhone(patient.getPhone());
        return dto;
    }
}
