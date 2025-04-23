package com.project.vetdata.controller;

import com.project.vetdata.dto.HealthRecordCreateDTO;
import com.project.vetdata.dto.HealthRecordResponseDTO;
import com.project.vetdata.dto.HealthRecordUpdateDTO;
import com.project.vetdata.model.HealthRecord;
import com.project.vetdata.service.HealthRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.stream.Collectors;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/health-records")
public class HealthRecordController {

    private final HealthRecordService healthRecordService;

    public HealthRecordController(HealthRecordService healthRecordService) {
        this.healthRecordService =healthRecordService;
    }

    @Operation(summary = "Criar um novo prontuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Prontuário criado com sucesso!",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = HealthRecord.class))}),
            @ApiResponse(responseCode = "400", description = "Dados inválidos!", content = @Content)
    })
    @PostMapping
    public ResponseEntity<HealthRecord> createHealthRecord(@Valid @RequestBody HealthRecordCreateDTO dto) {
        HealthRecord created = healthRecordService.createHealthRecord(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(summary = "Atualizar um prontuário existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Prontuário atualizado com sucesso!",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = HealthRecord.class))}),
            @ApiResponse(responseCode = "400", description = "Dados inválidos!", content = @Content),
            @ApiResponse(responseCode = "404", description = "Prontuário não encontrado!", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<HealthRecordResponseDTO> updateHealthRecord(@PathVariable Long id, @Valid @RequestBody HealthRecordUpdateDTO dto) {
        HealthRecord updated = healthRecordService.updateHealthRecord(id, dto);
        return ResponseEntity.ok(new HealthRecordResponseDTO(updated));
    }

    @Operation(summary = "Buscar todos os prontuários médicos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Prontuários encontrados!",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = HealthRecord.class))}),
            @ApiResponse(responseCode = "204", description = "Nenhum prontuário encontrado!", content = @Content)
    })
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllHealthRecords(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int qtdRecordsPage,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(required = false) String search
    ) {
        Pageable paging = PageRequest.of(page, qtdRecordsPage, Sort.by(sortBy));
        Page<HealthRecord> pageHealthRecords;

        if (search != null && !search.isBlank()) {
            pageHealthRecords = healthRecordService.searchByPatient(search, paging);
        } else {
            pageHealthRecords = healthRecordService.findAll(paging);
        }

        if (pageHealthRecords.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        List<HealthRecordResponseDTO> responseDTOs = pageHealthRecords
                .getContent()
                .stream()
                .map(HealthRecordResponseDTO::new)
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("total", pageHealthRecords.getTotalElements());
        response.put("qtdRecordsPage", pageHealthRecords.getSize());
        response.put("page", pageHealthRecords.getNumber());
        response.put("data", responseDTOs);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Remover prontuário por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Prontuário removido!"),
            @ApiResponse(responseCode = "404", description = "Prontuário não encontrado!", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHealthRecord(@PathVariable Long id) {
        return healthRecordService.getHealthRecordById(id)
                .map(this::handleDelete)
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    private ResponseEntity<Void> handleDelete(HealthRecord record) {
        healthRecordService.deleteHealthRecord(record.getId());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "Buscar prontuário pelo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Prontuário encontrado!",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = HealthRecordResponseDTO.class))}),
            @ApiResponse(responseCode = "400", description = "ID inválido!", content = @Content),
            @ApiResponse(responseCode = "404", description = "Prontuário não encontrado!", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getHealthRecordById(@PathVariable Long id) {
        return healthRecordService.getHealthRecordById(id)
                .map(this::convertToResponseEntity)
                .orElse(ResponseEntity.notFound().build());
    }

    private ResponseEntity<?> convertToResponseEntity(HealthRecord record) {
        return ResponseEntity.ok(new HealthRecordResponseDTO(record));
    }
}
