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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
