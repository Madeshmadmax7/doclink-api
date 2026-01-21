package com.example.backend.service;

import com.example.backend.entity.DoctorNote;
import com.example.backend.entity.MedicalReport;
import com.example.backend.entity.User;
import com.example.backend.repository.DoctorNoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DoctorNoteService {

    private final DoctorNoteRepository noteRepository;

    public DoctorNote add(MedicalReport report, User doctor, String content) {

        if (!report.getAppointment().getDoctor().getId().equals(doctor.getId())) {
            throw new SecurityException("Not your patient");
        }

        return noteRepository.save(
                DoctorNote.builder()
                        .report(report)
                        .doctor(doctor)
                        .content(content)
                        .build()
        );
    }

    public List<DoctorNote> getNotes(Long reportId) {
        return noteRepository.findByReportIdOrderByCreatedAtAsc(reportId);
    }
}
