package com.project.vetdata.dto;

import com.project.vetdata.enums.AuthenticationStatus;

public record AuthenticationResponseDTO(AuthenticationStatus status, String name) {}
