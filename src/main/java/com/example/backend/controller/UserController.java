package com.example.backend.controller;

import com.example.backend.dto.UserDTO;
import com.example.backend.entity.Role;
import com.example.backend.entity.User;
import com.example.backend.mapper.UserMapper;
import com.example.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping
    public UserDTO create(@RequestBody User user) {
        return userMapper.toDto(userService.save(user));
    }

    @GetMapping("/id/{id}")
    public UserDTO getById(@PathVariable Long id) {
        return userMapper.toDto(userService.getById(id));
    }

    @GetMapping("/role/{role}")
    public List<UserDTO> getByRole(@PathVariable Role role) {
        return userService.getByRole(role)
                .stream()
                .map(userMapper::toDto)
                .toList();
    }
}
