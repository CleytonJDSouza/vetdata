package com.project.vetdata.dto;

import jakarta.validation.constraints.NotBlank;

public class PostOperativeCreateDTO {

    @NotBlank(message = "Campo Obrigatório")
    private String description;

    public PostOperativeCreateDTO() {
    }

    public PostOperativeCreateDTO(String description) {
        this.description = description;

    }

    public String getDescription() {
        return description;
    }
}
