package com.project.vetdata.controller;

import com.project.vetdata.dto.DiagnosticCreateDTO;
import com.project.vetdata.model.Diagnostic;
import com.project.vetdata.service.DiagnosticService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/diagnostics")
public class DiagnosticController {
    private final DiagnosticService service;

    public DiagnosticController(DiagnosticService service) {
        this.service = service;
    }

    @Operation(summary = "Criar um novo diagnóstico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Diagnóstico criado com sucesso!",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Diagnostic.class))}),
            @ApiResponse(responseCode = "400", description = "Dados inválidos!", content = @Content)
    })
    @PostMapping
    public ResponseEntity<Diagnostic> createDiagnostic(@Valid @RequestBody DiagnosticCreateDTO dto) {
        Diagnostic createdDiagnostic = service.createDiagnostic(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdDiagnostic);
    }

    @Operation(summary = "Remover diagnóstico por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Diagnóstico removido!"),
            @ApiResponse(responseCode = "404", description = "Diagnóstico não encontrado!", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDiagnostic(@PathVariable Long id) {
        return service.getDiagnosticById(id)
                .map(this::handleDelete)
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    private ResponseEntity<Void> handleDelete(Diagnostic diagnostic)  {
        service.deleteDiagnostic(diagnostic.getId());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "Listar todos os diagnósticos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de diagnósticos retornada com sucesso!",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Diagnostic.class))})
    })
    @GetMapping
    public ResponseEntity<List<Diagnostic>> getAllDiagnostics() {
        List<Diagnostic> diagnostics = service.getAllDiagnostics();
        return ResponseEntity.ok(diagnostics);
    }
}
