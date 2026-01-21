package com.example.backend.controller;

import com.example.backend.dto.DoctorNoteDTO;
import com.example.backend.entity.MedicalReport;
import com.example.backend.entity.User;
import com.example.backend.mapper.DoctorNoteMapper;
import com.example.backend.service.DoctorNoteService;
import com.example.backend.service.MedicalReportService;
import com.example.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notes")
@RequiredArgsConstructor
public class DoctorNoteController {

        private final DoctorNoteService noteService;
        private final MedicalReportService reportService;
        private final UserService userService;
        private final DoctorNoteMapper mapper;

        @PreAuthorize("hasRole('DOCTOR')")
        @PostMapping("/appointment/{appointmentId}")
        public DoctorNoteDTO add(
                        @PathVariable Long appointmentId,
                        @RequestBody String content,
                        Authentication auth) {

                User doctor = userService.getByEmail(auth.getName());

                MedicalReport report = reportService.createIfNotExists(appointmentId, doctor);

                return mapper.toDto(
                                noteService.add(report, doctor, content));
        }

        @PreAuthorize("hasAnyRole('DOCTOR','PATIENT')")
        @GetMapping("/report/{reportId}")
        public List<DoctorNoteDTO> get(@PathVariable Long reportId) {

                return noteService.getNotes(reportId)
                                .stream()
                                .map(mapper::toDto)
                                .toList();
        }
}
