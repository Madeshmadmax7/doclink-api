package com.example.backend.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AppointmentDTO {

    private Long id;

    // Patient
    private Long patientId;
    private String patientName;

    // Doctor
    private Long doctorId;
    private String doctorName;

    // Nurse
    private Long nurseId;
    private String nurseName;

    // Times
    private LocalDateTime requestedTime;
    private LocalDateTime appointmentTime;

    private Integer durationMinutes;
    private String status;
}
