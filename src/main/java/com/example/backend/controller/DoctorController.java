package com.example.backend.controller;

import com.example.backend.dto.DoctorDTO;
import com.example.backend.entity.User;
import com.example.backend.mapper.DoctorMapper;
import com.example.backend.service.DoctorService;
import com.example.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/doctor")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorService doctorService;
    private final UserService userService;
    private final DoctorMapper doctorMapper;

    @PreAuthorize("hasRole('DOCTOR')")
    @GetMapping("/me")
    public DoctorDTO myProfile(Authentication authentication) {

        User user = userService.getByEmail(authentication.getName());

        return doctorMapper.toDto(
                doctorService.getOrCreateDoctor(user)
        );
    }

    @PreAuthorize("hasRole('DOCTOR')")
    @PutMapping("/me")
    public DoctorDTO updateProfile(
            Authentication authentication,
            @RequestBody DoctorDTO dto) {

        User user = userService.getByEmail(authentication.getName());
        var doctor = doctorService.getOrCreateDoctor(user);

        doctor.setExperienceYears(dto.getExperienceYears());
        doctor.setSpeciality(dto.getSpeciality());

        return doctorMapper.toDto(doctor);
    }
}
