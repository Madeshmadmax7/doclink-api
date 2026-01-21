package com.example.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backend.entity.DoctorNote;

public interface DoctorNoteRepository extends JpaRepository<DoctorNote, Long> {

    List<DoctorNote> findByReportIdOrderByCreatedAtAsc(Long reportId);
}