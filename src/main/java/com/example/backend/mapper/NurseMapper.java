package com.example.backend.mapper;

import com.example.backend.dto.NurseDTO;
import com.example.backend.entity.Nurse;
import org.springframework.stereotype.Component;

@Component
public class NurseMapper {

    public NurseDTO toDto(Nurse nurse) {
        if (nurse == null) return null;

        NurseDTO dto = new NurseDTO();
        dto.setId(nurse.getId());
        dto.setName(nurse.getUser().getName());
        dto.setEmail(nurse.getUser().getEmail());
        dto.setDepartment(nurse.getDepartment());
        dto.setShift(nurse.getShift());
        dto.setExperienceYears(nurse.getExperienceYears());
        return dto;
    }
}
