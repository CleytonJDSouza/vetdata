package com.project.vetdata.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DogBreedExternalDTO {

    @JsonProperty("id")
    private String idExternalApi;
    @JsonProperty("attributes")
    private AttributesDTO attributesDTO;

    public DogBreedExternalDTO() {
    }


    public String getIdExternalApi() {
        return idExternalApi;
    }

    public void setIdExternalApi(String idExternalApi) {
        this.idExternalApi = idExternalApi;
    }

    public AttributesDTO getAttributeDTO() {
        return attributesDTO;
    }

    public void setAttributeDTO(AttributesDTO attributesDTO) {
        this.attributesDTO = attributesDTO;
    }
}
