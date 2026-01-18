package com.example.backend.mapper;

import com.example.backend.dto.DoctorDTO;
import com.example.backend.entity.Doctor;
import org.springframework.stereotype.Component;

@Component
public class DoctorMapper {

    public DoctorDTO toDto(Doctor doctor) {
        DoctorDTO dto = new DoctorDTO();
        dto.setId(doctor.getId());
        dto.setName(doctor.getUser().getName());
        dto.setEmail(doctor.getUser().getEmail());
        dto.setSpeciality(doctor.getSpeciality());
        dto.setExperienceYears(doctor.getExperienceYears());
        dto.setActive(doctor.isActive());
        dto.setProfileUrlLink(doctor.getUser().getProfileUrlLink());
        return dto;
    }
}
