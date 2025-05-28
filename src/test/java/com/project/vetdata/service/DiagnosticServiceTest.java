package com.project.vetdata.service;

import com.project.vetdata.dto.DiagnosticCreateDTO;
import com.project.vetdata.mappers.MapperDTOToEntity;
import com.project.vetdata.model.Diagnostic;
import com.project.vetdata.repository.DiagnosticRepository;
import com.project.vetdata.templates.DiagnosticTemplate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DiagnosticServiceTest {
    @Mock
    private DiagnosticRepository repository;

    @Mock
    private MapperDTOToEntity<DiagnosticCreateDTO, Diagnostic> mapper;

    @InjectMocks
    private DiagnosticService service;

    @Test
    public void given_valid_diagnosticCreateDTO_when_createDiagnostic_is_called_then_diagnostic_is_saved_and_returned(){
        DiagnosticCreateDTO createDTO = DiagnosticTemplate.getFakeDiagnosticCreateDTO();
        Diagnostic newDiagnostic = DiagnosticTemplate.getFakeDiagnostic();

        when(mapper.map(any(DiagnosticCreateDTO.class))).thenReturn(newDiagnostic);
        when(repository.save(any(Diagnostic.class))).thenReturn(newDiagnostic);

        Diagnostic savedDiagnostic = service.createDiagnostic(createDTO);
        assertNotNull(savedDiagnostic);

        verify(repository, times(1)).save(newDiagnostic);
    }

    @Test
    public void given_valid_is_when_deleteDiagnostic_is_called_then_diagnostic_is_deleted() {
        Long diagnosticID = 1L;

        service.deleteDiagnostic(diagnosticID);

        verify(repository, times(1)).deleteById(diagnosticID);
    }

    @Test
    public void given_diagnostics_exist_when_getAllDiagnostics_is_called_then_return_list_of_diagnostics() {
        List<Diagnostic> mockDiagnostic = List.of(DiagnosticTemplate.getFakeDiagnostic());

        when(repository.findAll()).thenReturn(mockDiagnostic);
        List<Diagnostic> result = service.getAllDiagnostics();

        assertNotNull(result, "A lista não pode ser nula.");
        assertEquals(1, result.size(), "A lista deve conter 1 diagnostic");
        verify(repository, times(1)).findAll();
    }

    @Test
    public void given_no_diagnostics_when_getAllDiagnostics_is_called_then_return_empty_list() {
        when(repository.findAll()).thenReturn(Collections.emptyList());

        List<Diagnostic> result = service.getAllDiagnostics();

        assertNotNull(result, "A lista não deve ser nula.");
        assertTrue(result.isEmpty(), "A lista deve estar vazia");
        verify(repository, times(1)).findAll();
    }

    @Test
    public void given_diagnostic_exist_when_getAllDiagnosticsPaginated_is_called_then_return_page_of_diagnostics() {
        Pageable pageable = PageRequest.of(0, 10);
        Diagnostic diagnostic = DiagnosticTemplate.getFakeDiagnostic();
        Page<Diagnostic> diagnosticPage = new PageImpl<>(List.of(diagnostic));

        when(repository.findAll(pageable)).thenReturn(diagnosticPage);

        Page<Diagnostic> result = service.getAllDiagnosticsPaginated(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(repository, times(1)).findAll(pageable);
    }

    @Test
    public void given_matching_diagnostics_when_getBySearchTerm_is_called_then_return_page_of_matching_diagnostics() {
        String term = "gripe";
        Pageable pageable = PageRequest.of(0,10);
        Diagnostic diagnostic = DiagnosticTemplate.getFakeDiagnostic();
        Page<Diagnostic> diagnosticPage = new PageImpl<>(List.of(diagnostic));

        when(repository.findByDescriptionContainingIgnoreCase(term, pageable)).thenReturn(diagnosticPage);

        Page<Diagnostic> result = service.getBySearchTerm(term, pageable);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.getTotalElements());
        verify(repository, times(1)).findByDescriptionContainingIgnoreCase(term, pageable);
    }

    @Test
    public void given_no_matching_diagnostics_when_getBySearchTerm_is_called_then_return_empty_page() {
        String term = "teste";
        Pageable pageable = PageRequest.of(0,10);
        Page<Diagnostic> emptyPage = Page.empty(pageable);

        when(repository.findByDescriptionContainingIgnoreCase(term, pageable)).thenReturn(emptyPage);

        Page<Diagnostic> result = service.getBySearchTerm(term, pageable);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(repository, times(1)).findByDescriptionContainingIgnoreCase(term, pageable);
    }
}
