package com.project.vetdata.dto;

import jakarta.validation.constraints.NotBlank;

public class DiagnosticCreateDTO {

    @NotBlank(message = "Campo Obrigatório")
    private String description;

    private String observation;

    public DiagnosticCreateDTO() {
    }

    public DiagnosticCreateDTO(String description, String observation) {
        this.description = description;
        this.observation = observation;
    }

    public String getDescription() {
        return description;
    }


    public String getObservation() {
        return observation;
    }
}
