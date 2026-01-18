package com.example.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.example.backend.entity.Role;
import com.example.backend.entity.User;
import com.example.backend.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User save(User user) {
        return userRepository.save(user);
    }

    public User getById(Long id) {
        return userRepository.findById(id).orElseThrow();
    }

    public User getByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow();
    }

    public List<User> getByRole(Role role) {
        return userRepository.findByRole(role);
    }

    public List<User> getPendingStaff() {
        return userRepository.findByRoleInAndApprovedFalse(
                List.of(Role.DOCTOR, Role.NURSE)
        );
    }

    public void approveUser(Long userId) {
        User user = getById(userId);
        user.setApproved(true);
        userRepository.save(user);
    }

    public void rejectUser(Long userId) {
        userRepository.deleteById(userId);
    }
}
