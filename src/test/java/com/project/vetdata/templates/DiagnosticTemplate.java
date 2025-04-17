package com.project.vetdata.templates;

import com.project.vetdata.dto.DiagnosticCreateDTO;
import com.project.vetdata.model.Diagnostic;

public final class DiagnosticTemplate {

    public static DiagnosticCreateDTO getFakeDiagnosticCreateDTO() {
        return new DiagnosticCreateDTO("Cancer", "test");
    }

    public static Diagnostic getFakeDiagnostic() {
        return new Diagnostic("Cancer", "test");
    }

    public static DiagnosticCreateDTO getFakeDiagnosticCreateDTOWithInvalidDescription() {
        return new DiagnosticCreateDTO();
    }
}
