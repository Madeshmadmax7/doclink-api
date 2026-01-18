package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "doctors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Doctor {

    @Id
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private User user;

    @Column(nullable = false)
    private String speciality;

    private String licenseNumber;
    private Integer experienceYears;

    private boolean active = true;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private DoctorSlot slot;

}
