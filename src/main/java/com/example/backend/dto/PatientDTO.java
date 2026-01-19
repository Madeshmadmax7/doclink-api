package com.example.backend.dto;

import lombok.Data;

@Data
public class PatientDTO {
    private Long id;
    private String name;
    private String email;
    private String gender;
    private Integer age;
    private String bloodGroup;
    private String phone;
}
