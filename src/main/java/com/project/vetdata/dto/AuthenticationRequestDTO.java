package com.project.vetdata.dto;

import jakarta.validation.constraints.NotBlank;

public record AuthenticationRequestDTO(@NotBlank String email, @NotBlank String password) {}
