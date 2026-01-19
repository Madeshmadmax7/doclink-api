package com.example.backend.controller;

import com.example.backend.entity.Nurse;
import com.example.backend.entity.User;
import com.example.backend.service.NurseService;
import com.example.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/nurse")
@RequiredArgsConstructor
public class NurseController {

    private final NurseService nurseService;
    private final UserService userService;

    @PreAuthorize("hasRole('NURSE')")
    @GetMapping("/me")
    public Nurse myProfile(Authentication authentication) {
        User user = userService.getByEmail(authentication.getName());
        return nurseService.getOrCreateNurse(user);
    }
}
