package com.project.vetdata.controller;

import com.project.vetdata.dto.HealthRecordResponseDTO;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/hospital-admission")
public class HospitalAdmissionController {

    private final HospitalAdmissionService hospitalAdmissionService;

    public HospitalAdmissionController(HospitalAdmissionService hospitalAdmissionService) {
        this.hospitalAdmissionService = hospitalAdmissionService;
    }

    @Operation(summary = "Criar uma nova internação")
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

    @Operation(summary = "Listar todas as internações")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de internações retornada com sucesso!",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = HospitalAdmission.class))})
    })
    @GetMapping
    public ResponseEntity<List<HospitalAdmissionResponseDTO>> getAllHospitalAdmission() {
        List<HospitalAdmissionResponseDTO> admissions = hospitalAdmissionService.findAll();
        return  ResponseEntity.ok(admissions);
    }

    @Operation(summary = "Remover internação por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Internação removida!"),
            @ApiResponse(responseCode = "404", description = "Intenação não encontrada!", content = @Content)
    })
    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteHospitalAdmission(@PathVariable Long id) {
        return hospitalAdmissionService.getHospitalAdmissionById(id)
                .map(this::handleDelete)
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    private ResponseEntity<Void> handleDelete(HospitalAdmission admission) {
        hospitalAdmissionService.deleteHospitalAdmission(admission.getId());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "Buscar internação pelo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Internação encontrada!",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = HealthRecordResponseDTO.class))}),
            @ApiResponse(responseCode = "400", description = "ID inválido!", content = @Content),
            @ApiResponse(responseCode = "404", description = "Internação não encontrada!", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getHospitalAdmissionById(@PathVariable Long id) {
        return hospitalAdmissionService.getHospitalAdmissionById(id)
                .map(this::convertToResponseEntity)
                .orElse(ResponseEntity.notFound().build());
    }

    private ResponseEntity<?> convertToResponseEntity(HospitalAdmission admission) {
        return ResponseEntity.ok(new HospitalAdmissionResponseDTO(admission));
    }
}
