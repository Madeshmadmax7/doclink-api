package com.example.backend.service;

import com.example.backend.entity.Doctor;
import com.example.backend.entity.DoctorLeave;
import com.example.backend.entity.Role;
import com.example.backend.entity.User;
import com.example.backend.entity.DoctorSlot;
import com.example.backend.repository.DoctorLeaveRepository;
import com.example.backend.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DoctorService {

    private final DoctorRepository doctorRepo;
    private final DoctorLeaveRepository leaveRepo;

    public Doctor getOrCreateDoctor(User user) {

        if (user == null) {
            throw new IllegalStateException("Doctor user is null");
        }

        if (user.getRole() != Role.DOCTOR) {
            throw new IllegalStateException("User is not a doctor");
        }

        if (!user.isApproved()) {
            throw new IllegalStateException("Doctor not approved by admin");
        }

        return doctorRepo.findById(user.getId())
                .orElseGet(() -> {
                    Doctor doctor = Doctor.builder()
                            .user(user)
                            .speciality(
                                    user.getSpeciality() != null
                                            ? user.getSpeciality()
                                            : "GENERAL"
                            )
                            .slot(DoctorSlot.MORNING) // 🔥 REQUIRED
                            .active(true)
                            .build();
                    return doctorRepo.save(doctor);
                });
    }

    public Doctor getDoctorById(Long doctorId) {
        return doctorRepo.findById(doctorId)
                .orElseThrow(() ->
                        new IllegalStateException("Doctor not found"));
    }

    public List<DoctorLeave> getLeaves(Long doctorId) {
        return leaveRepo.findByDoctorId(doctorId);
    }
}
