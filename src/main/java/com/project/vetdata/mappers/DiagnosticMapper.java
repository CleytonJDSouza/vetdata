package com.project.vetdata.mappers;

import com.project.vetdata.dto.DiagnosticCreateDTO;
import com.project.vetdata.model.Diagnostic;
import org.springframework.stereotype.Component;

@Component
public class DiagnosticMapper implements MapperDTOToEntity<DiagnosticCreateDTO, Diagnostic>{

    @Override
    public Diagnostic map(DiagnosticCreateDTO dto) {
        return new Diagnostic(dto.getDescription(), dto.getObservation());
    }
}
