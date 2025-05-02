package com.project.vetdata.controller;

import com.project.vetdata.dto.HospitalAdmissionCreateDTO;
import com.project.vetdata.dto.HospitalAdmissionResponseDTO;
import com.project.vetdata.dto.HospitalAdmissionUpdateDTO;
import com.project.vetdata.model.HospitalAdmission;
import com.project.vetdata.service.HospitalAdmissionService;
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
@RequestMapping("/hospital-admission")
public class HospitalAdmissionController {

    private final HospitalAdmissionService hospitalAdmissionService;

    public HospitalAdmissionController(HospitalAdmissionService hospitalAdmissionService) {
        this.hospitalAdmissionService = hospitalAdmissionService;
    }

    @Operation(summary = "Criar uma nova internação hospitalar")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Internação criada com sucesso!",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = HospitalAdmissionResponseDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Dados inválidos!", content = @Content)
    })
    @PostMapping
    public ResponseEntity<HospitalAdmissionResponseDTO> createHospitalAdmission(@Valid @RequestBody HospitalAdmissionCreateDTO dto) {
        HospitalAdmissionResponseDTO response = hospitalAdmissionService.createHospitalAdmission(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Atualizar uma internação existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Internação atualizada com sucesso!",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = HospitalAdmissionResponseDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Dados inválidos!", content = @Content),
            @ApiResponse(responseCode = "404", description = "Internação hospitalar não encontrada!", content = @Content)
    })

    @PutMapping("/{id}")
    public ResponseEntity<HospitalAdmissionResponseDTO> updateHospitalAdmission(
            @PathVariable Long id,
            @Valid @RequestBody HospitalAdmissionUpdateDTO dto) {

        HospitalAdmissionResponseDTO updated = hospitalAdmissionService.updateHospitalAdmission(id, dto);
        return ResponseEntity.ok(updated);
    }
}
