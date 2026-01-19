package com.example.backend.controller;

import com.example.backend.entity.Patient;
import com.example.backend.entity.User;
import com.example.backend.service.PatientService;
import com.example.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/patient")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;
    private final UserService userService;

    @PreAuthorize("hasRole('PATIENT')")
    @GetMapping("/me")
    public Patient myProfile(Authentication authentication) {
        User user = userService.getByEmail(authentication.getName());
        return patientService.getOrCreatePatient(user);
    }
}
