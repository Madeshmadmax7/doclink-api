package com.example.backend.service;

import com.example.backend.entity.*;
import com.example.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepo;
    private final DoctorRepository doctorRepo;
    private final NurseRepository nurseRepo;
    private final PatientRepository patientRepo;

    public List<User> getPendingStaff() {
        return userRepo.findByApprovedFalseAndRoleIn(
                List.of(Role.DOCTOR, Role.NURSE)
        );
    }

    @Transactional
    public void approveUser(Long userId) {

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new IllegalStateException("User not found"));

        user.setApproved(true);

        switch (user.getRole()) {

            case DOCTOR -> doctorRepo.findById(user.getId())
                    .orElseGet(() ->
                            doctorRepo.save(
                                    Doctor.builder()
                                            .user(user)
                                            .speciality(user.getSpeciality())
                                            .slot(DoctorSlot.MORNING)
                                            .active(true)
                                            .build()
                            )
                    );

            case NURSE -> nurseRepo.findById(user.getId())
                    .orElseGet(() ->
                            nurseRepo.save(
                                    Nurse.builder()
                                            .user(user)
                                            .build()
                            )
                    );

            case PATIENT -> patientRepo.findById(user.getId())
                    .orElseGet(() ->
                            patientRepo.save(
                                    Patient.builder()
                                            .user(user)
                                            .build()
                            )
                    );
        }
    }

    // ================= REJECT =================
    @Transactional
    public void rejectUser(Long userId) {
        userRepo.deleteById(userId);
    }
}
