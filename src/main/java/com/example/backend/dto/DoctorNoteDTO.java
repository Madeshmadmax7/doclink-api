package com.example.backend.dto;


import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DoctorNoteDTO {
    private Long id;
    private Long reportId;
    private Long doctorId;
    private String doctorName;
    private String content;
    private LocalDateTime createdAt;
}