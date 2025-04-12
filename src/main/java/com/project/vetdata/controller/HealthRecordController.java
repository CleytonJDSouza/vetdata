package com.project.vetdata.controller;

import com.project.vetdata.dto.HealthRecordCreateDTO;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
