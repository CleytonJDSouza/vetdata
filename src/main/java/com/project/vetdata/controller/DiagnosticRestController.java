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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/diagnostics")
public class DiagnosticRestController {

    private static final Logger logger = LoggerFactory.getLogger(DiagnosticRestController.class);
    private final DiagnosticService service;

    public DiagnosticRestController(DiagnosticService service) {
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
        try {
            Diagnostic createdDiagnostic = service.createDiagnostic(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdDiagnostic);
        } catch (Exception e) {
            logger.error("Erro ao criar diagnóstico para paciente descrição='{}'", dto.getDescription(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
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
                .orElseGet(() -> {
                    logger.warn("Diagnóstico com ID {} não encontrado para remoção", id);
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                });
    }

    private ResponseEntity<Void> handleDelete(Diagnostic diagnostic) {
        try {
            service.deleteDiagnostic(diagnostic.getId());
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            logger.error("Erro ao remover diagnóstico com ID: {}", diagnostic.getId(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
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
        logger.debug("Listando todos os diagnósticos. Total encontrados: {}", diagnostics.size());
        return ResponseEntity.ok(diagnostics);
    }

    @Operation(summary = "Buscar todos os diagnósticos (com paginação e busca)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Diagnóstico(s) encontrado(s)!",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Diagnostic.class))}),
            @ApiResponse(responseCode = "204", description = "Nenhum diagnóstico encontrado!", content = @Content)
    })
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> getAllDiagnosticsPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int qtdRecordsPage,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(required = false) String searchByTerm
    ) {
        Pageable pageable = PageRequest.of(page, qtdRecordsPage, Sort.by(sortBy));
        Page<Diagnostic> diagnosticsPage = StringUtils.hasText(searchByTerm)
                ? service.getBySearchTerm(searchByTerm, pageable)
                : service.getAllDiagnosticsPaginated(pageable);

        if (diagnosticsPage.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        Map<String, Object> response = Map.of(
                "total", diagnosticsPage.getTotalElements(),
                "qtdRecordsPage", diagnosticsPage.getSize(),
                "page", diagnosticsPage.getNumber(),
                "data", diagnosticsPage.getContent()
        );

        logger.debug("Total de diagnósticos encontrados: {}", diagnosticsPage.getTotalElements());

        return ResponseEntity.ok(response);
    }
}
