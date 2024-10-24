package com.project.vetdata.dto;


import com.fasterxml.jackson.annotation.JsonProperty;

public class AttributesDTO {

    private String name;
    private String description;
    private LifeDTO life;
    @JsonProperty("male_weight")
    private MaleWeightDTO maleWeightDTO;
    @JsonProperty("female_weight")
    private FemaleWeightDTO femaleWeightDTO;
    private Boolean hypoallergenic;

    public AttributesDTO() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LifeDTO getLife() {
        return life;
    }

    public void setLife(LifeDTO life) {
        this.life = life;
    }

    public MaleWeightDTO getMaleWeightDTO() {
        return maleWeightDTO;
    }

    public void setMaleWeightDTO(MaleWeightDTO maleWeightDTO) {
        this.maleWeightDTO = maleWeightDTO;
    }

    public FemaleWeightDTO getFemaleWeightDTO() {
        return femaleWeightDTO;
    }

    public void setFemaleWeightDTO(FemaleWeightDTO femaleWeightDTO) {
        this.femaleWeightDTO = femaleWeightDTO;
    }

    public Boolean getHypoallergenic() {
        return hypoallergenic;
    }

    public void setHypoallergenic(Boolean hypoallergenic) {
        this.hypoallergenic = hypoallergenic;
    }
}
