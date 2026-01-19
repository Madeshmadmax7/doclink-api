package com.example.backend.service;

import com.example.backend.entity.Patient;
import com.example.backend.entity.Role;
import com.example.backend.entity.User;
import com.example.backend.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;

    public Patient getOrCreatePatient(User user) {

        if (user.getRole() != Role.PATIENT) {
            throw new IllegalStateException("User is not a patient");
        }

        return patientRepository.findById(user.getId())
                .orElseGet(() -> {
                    Patient patient = Patient.builder()
                            .user(user)
                            .build();
                    return patientRepository.save(patient);
                });
    }
}
