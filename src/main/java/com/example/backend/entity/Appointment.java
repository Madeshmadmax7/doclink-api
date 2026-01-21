package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "appointments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Patient always exists
    @ManyToOne(optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private User patient;

    // Assigned by nurse later
    @ManyToOne
    @JoinColumn(name = "nurse_id")
    private User nurse;

    // Assigned by nurse later
    @ManyToOne
    @JoinColumn(name = "doctor_id")
    private User doctor;

    // Set only when approved
    @Column(nullable = true)
    private LocalDateTime appointmentTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AppointmentStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private Integer durationMinutes;

    @Column(nullable = true)
    private LocalDateTime requestedTime;

    @Column(nullable = true)
    private String nurseMessage;

    @PrePersist
    void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = AppointmentStatus.REQUESTED;
        }
    }
}
