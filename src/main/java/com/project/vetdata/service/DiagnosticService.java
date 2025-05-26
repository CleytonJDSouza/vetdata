package com.project.vetdata.service;

import com.project.vetdata.dto.DiagnosticCreateDTO;
import com.project.vetdata.mappers.MapperDTOToEntity;
import com.project.vetdata.model.Diagnostic;
import com.project.vetdata.repository.DiagnosticRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class DiagnosticService {
    private final DiagnosticRepository repository;
    private final MapperDTOToEntity<DiagnosticCreateDTO, Diagnostic> mapper;

    public DiagnosticService(DiagnosticRepository repository, @Qualifier("diagnosticMapper") MapperDTOToEntity<DiagnosticCreateDTO, Diagnostic> mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public Diagnostic createDiagnostic(DiagnosticCreateDTO dto) {
        return repository.save(mapper.map(dto));
    }

    public void deleteDiagnostic(Long id) {
        repository.deleteById(id);
    }

    public Optional<Diagnostic> getDiagnosticById(Long id) {
        return repository.findById(id);
    }

    public List<Diagnostic> getAllDiagnostics() {
        return repository.findAll();
    }

    public Page<Diagnostic> getAllDiagnosticsPaginated(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<Diagnostic> getBySearchTerm(String term, Pageable pageable) {
        return repository.findByDescriptionContainingIgnoreCase(term, pageable);
    }
}