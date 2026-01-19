package com.example.backend.dto;

import com.example.backend.entity.NurseShift;
import lombok.Data;

@Data
public class NurseDTO {
    private Long id;
    private String name;
    private String email;
    private String department;
    private NurseShift shift;
    private Integer experienceYears;
}
