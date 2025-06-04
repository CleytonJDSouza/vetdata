package com.project.vetdata.controller;

import com.project.vetdata.dto.AuthenticationRequestDTO;
import com.project.vetdata.dto.AuthenticationResponseDTO;
import com.project.vetdata.enums.AuthenticationStatus;
import com.project.vetdata.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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
    public ResponseEntity<AuthenticationResponseDTO> authenticate(@RequestBody @Valid AuthenticationRequestDTO dto, HttpSession session) {
        AuthenticationResponseDTO response = authenticationService.authenticate(dto);
        if (response.status().equals(AuthenticationStatus.AUTHORIZED)){
            session.setAttribute("user", response.name());
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user-info")
    public ResponseEntity<?> getUserInfo(HttpSession session) {
        String username = (String) session.getAttribute("user");
        if (username != null) {
            return ResponseEntity.ok(Map.of("username", username.split(" ")[0]));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuário não autenticado");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok().build(); // 200 OK
    }
}
