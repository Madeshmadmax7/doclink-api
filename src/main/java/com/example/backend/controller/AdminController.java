package com.example.backend.controller;

import com.example.backend.dto.UserDTO;
import com.example.backend.entity.DoctorLeave;
import com.example.backend.mapper.UserMapper;
import com.example.backend.service.AdminService;
import com.example.backend.service.DoctorLeaveService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final UserMapper userMapper;
    private final DoctorLeaveService doctorLeaveService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/pending-staff")
    public List<UserDTO> pendingStaff() {
        return adminService.getPendingStaff()
                .stream()
                .map(userMapper::toDto)
                .toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/approve/{userId}")
    public void approve(@PathVariable Long userId) {
        adminService.approveUser(userId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/reject/{userId}")
    public void reject(@PathVariable Long userId) {
        adminService.rejectUser(userId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/doctor/{doctorId}/leaves")
    public List<DoctorLeave> getDoctorLeaves(@PathVariable Long doctorId) {
        return doctorLeaveService.getLeaves(doctorId);
    }
}
