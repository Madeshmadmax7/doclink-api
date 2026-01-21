package com.example.backend.mapper;

import com.example.backend.dto.DoctorNoteDTO;
import com.example.backend.entity.DoctorNote;
import org.springframework.stereotype.Component;

@Component
public class DoctorNoteMapper {

    public DoctorNoteDTO toDto(DoctorNote n) {
        if (n == null) return null;

        DoctorNoteDTO dto = new DoctorNoteDTO();
        dto.setId(n.getId());
        dto.setReportId(n.getReport().getId());
        dto.setDoctorId(n.getDoctor().getId());
        dto.setDoctorName(n.getDoctor().getName());
        dto.setContent(n.getContent());
        dto.setCreatedAt(n.getCreatedAt());
        return dto;
    }
}

