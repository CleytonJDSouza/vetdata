package com.project.vetdata.controller;

import com.project.vetdata.dto.AuthenticationRequestDTO;
import com.project.vetdata.dto.AuthenticationResponseDTO;
import com.project.vetdata.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/authentications")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Operation(summary = "Autenticar usuário com email e senha")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário autenticado com sucesso!",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AuthenticationResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Requisição inválida (campos obrigatórios não preenchidos)", content = @Content)
    })
    @PostMapping
    public ResponseEntity<AuthenticationResponseDTO> authenticate(@RequestBody @Valid AuthenticationRequestDTO dto) {
        AuthenticationResponseDTO response = authenticationService.authenticate(dto);
        return ResponseEntity.ok(response);
    }
}
