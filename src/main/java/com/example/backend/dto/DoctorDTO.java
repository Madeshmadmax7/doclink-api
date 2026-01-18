package com.example.backend.dto;

import lombok.Data;

@Data
public class DoctorDTO {
    private Long id;
    private String name;
    private String email;
    private String speciality;
    private Integer experienceYears;
    private boolean active;
    private String profileUrlLink;
}
