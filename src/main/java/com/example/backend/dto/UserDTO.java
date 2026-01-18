package com.example.backend.dto;

import com.example.backend.entity.Role;
import lombok.Data;

@Data
public class UserDTO {
    private Long id;
    private String name;
    private String email;
    private Role role;
    private String speciality;
    private boolean approved;
    private String profileUrlLink;

}
