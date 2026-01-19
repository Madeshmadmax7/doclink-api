package com.example.backend.service;

import com.example.backend.entity.Nurse;
import com.example.backend.entity.Role;
import com.example.backend.entity.User;
import com.example.backend.repository.NurseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NurseService {

    private final NurseRepository nurseRepository;

    public Nurse getOrCreateNurse(User user) {

        if (user.getRole() != Role.NURSE) {
            throw new IllegalStateException("User is not a nurse");
        }

        if (!user.isApproved()) {
            throw new IllegalStateException("Nurse not approved by admin");
        }

        return nurseRepository.findById(user.getId())
                .orElseGet(() -> {
                    Nurse nurse = Nurse.builder()
                            .user(user)
                            .build();
                    return nurseRepository.save(nurse);
                });
    }
}
