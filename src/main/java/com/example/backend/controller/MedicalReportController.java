package com.example.backend.controller;

import com.example.backend.dto.MedicalReportDTO;
import com.example.backend.entity.MedicalReport;
import com.example.backend.entity.Role;
import com.example.backend.entity.User;
import com.example.backend.mapper.MedicalReportMapper;
import com.example.backend.service.MedicalReportService;
import com.example.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class MedicalReportController {

        private final MedicalReportService reportService;
        private final MedicalReportMapper reportMapper;
        private final UserService userService;

        @PreAuthorize("hasRole('DOCTOR')")
        @PostMapping("/appointment/{appointmentId}")
        public MedicalReportDTO getOrCreate(
                        @PathVariable Long appointmentId,
                        Authentication auth) {

                User doctor = userService.getByEmail(auth.getName());
                return reportMapper.toDto(
                                reportService.createIfNotExists(appointmentId, doctor));
        }

        @PreAuthorize("hasRole('DOCTOR')")
        @PutMapping("/appointment/{appointmentId}")
        public MedicalReportDTO update(
                        @PathVariable Long appointmentId,
                        @RequestBody MedicalReportDTO dto,
                        Authentication auth) {

                User doctor = userService.getByEmail(auth.getName());
                return reportMapper.toDto(
                                reportService.updateReport(appointmentId, dto, doctor));
        }

        @PreAuthorize("hasRole('PATIENT')")
        @GetMapping("/appointment/{appointmentId}")
        public MedicalReportDTO getForPatient(
                        @PathVariable Long appointmentId,
                        Authentication auth) {

                User patient = userService.getByEmail(auth.getName());
                return reportMapper.toDto(
                                reportService.getForPatient(appointmentId, patient));
        }

        @PreAuthorize("hasRole('DOCTOR')")
        @GetMapping("/patient/{patientId}/history")
        public List<MedicalReportDTO> getPatientHistoryForDoctor(
                        @PathVariable Long patientId,
                        Authentication auth) {

                User doctor = userService.getByEmail(auth.getName());

                return reportService.getPatientHistory(patientId)
                                .stream()
                                .map(reportMapper::toDto)
                                .toList();

        }

        @PreAuthorize("hasRole('PATIENT')")
        @GetMapping("/me/history")
        public List<MedicalReportDTO> myHistory(Authentication auth) {

                User patient = userService.getByEmail(auth.getName());

                return reportService.getPatientHistory(patient.getId())
                                .stream()
                                .map(reportMapper::toDto)
                                .toList();
        }

        @PreAuthorize("hasAnyRole('DOCTOR','PATIENT')")
        @GetMapping("/appointment/{appointmentId}/current")
        public MedicalReportDTO getCurrentForCall(
                        @PathVariable Long appointmentId,
                        Authentication auth) {

                User user = userService.getByEmail(auth.getName());

                MedicalReport report;

                if (user.getRole() == Role.DOCTOR) {
                        report = reportService.createIfNotExists(appointmentId, user);
                } else {
                        report = reportService.getForPatient(appointmentId, user);
                }

                return reportMapper.toDto(report);
        }

}
