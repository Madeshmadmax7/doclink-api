package com.example.backend.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MedicalReportDTO {
    private Long id;
    private Long appointmentId;
    private String symptoms;
    private String diagnosis;
    private String prescription;
    private String advice;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
