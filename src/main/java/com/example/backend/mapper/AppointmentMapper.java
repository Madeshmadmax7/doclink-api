package com.example.backend.mapper;

import com.example.backend.dto.AppointmentDTO;
import com.example.backend.entity.Appointment;
import org.springframework.stereotype.Component;

@Component
public class AppointmentMapper {

    public AppointmentDTO toDto(Appointment a) {
        if (a == null) return null;

        AppointmentDTO dto = new AppointmentDTO();

        dto.setId(a.getId());
        dto.setStatus(a.getStatus().name());
        dto.setDurationMinutes(a.getDurationMinutes());

        dto.setPatientId(a.getPatient().getId());
        dto.setPatientName(a.getPatient().getName());

        if (a.getDoctor() != null) {
            dto.setDoctorId(a.getDoctor().getId());
            dto.setDoctorName(a.getDoctor().getName());
        }

        if (a.getNurse() != null) {
            dto.setNurseId(a.getNurse().getId());
            dto.setNurseName(a.getNurse().getName());
        }

        dto.setRequestedTime(a.getRequestedTime());
        dto.setAppointmentTime(a.getAppointmentTime());

        return dto;
    }
}
