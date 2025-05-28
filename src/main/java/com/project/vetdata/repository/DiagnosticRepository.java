package com.project.vetdata.repository;

import com.project.vetdata.model.Diagnostic;
import com.project.vetdata.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;

import java.util.Optional;

@Repository
public interface DiagnosticRepository extends JpaRepository<Diagnostic, Long> {
    Page<Diagnostic> findByDescriptionContainingIgnoreCase(String term, Pageable pageable);
}
