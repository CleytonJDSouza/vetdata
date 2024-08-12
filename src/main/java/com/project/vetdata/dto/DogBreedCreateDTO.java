package com.project.vetdata.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class DogBreedCreateDTO {

    private String name;
    private String description;
    private Integer lifeExpectancyMin;
    private Integer lifeExpectancyMax;
    private Double maleWeightMin;
    private Double maleWeightMax;
    private Double femaleWeightMin;
    private Double femaleWeightMax;
    private Boolean hypoallergenic;
    private String size;

    public DogBreedCreateDTO() {}

    public DogBreedCreateDTO(String name, String description, Integer lifeExpectancyMin, Integer lifeExpectancyMax, Double maleWeightMin, Double maleWeightMax, Double femaleWeightMin,
                             Double femaleWeightMax, Boolean hypoallergenic, String size) {
        this.name = name;
        this.description = description;
        this.lifeExpectancyMin = lifeExpectancyMin;
        this.lifeExpectancyMax = lifeExpectancyMax;
        this.maleWeightMin = maleWeightMin;
        this.maleWeightMax = maleWeightMax;
        this.femaleWeightMin = femaleWeightMin;
        this.femaleWeightMax = femaleWeightMax;
        this.hypoallergenic = hypoallergenic;
        this.size = size;
        }

    @NotBlank(message = "Campo Obrigatório")
    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    @NotNull(message = "Campo Obrigatório")
    @Min(value = 0, message = "Campo deve ter valor maior que: 0")
    public Integer getLifeExpectancyMin() {
        return lifeExpectancyMin;
    }

    @NotNull(message = "Campo Obrigatório")
    @Min(value = 0, message = "Campo deve ter valor maior que: 0")
    public Integer getLifeExpectancyMax() {
        return lifeExpectancyMax;
    }

    @NotNull(message = "Campo Obrigatório")
    @Min(value = 0, message = "Campo deve ter valor maior que: 0")
    public Double getMaleWeightMin() {
        return maleWeightMin;
    }

    @NotNull(message = "Campo Obrigatório")
    @Min(value = 0, message = "Campo deve ter valor maior que: 0")
    public Double getMaleWeightMax() {
        return maleWeightMax;
    }

    @NotNull(message = "Campo Obrigatório")
    @Min(value = 0, message = "Campo deve ter valor maior que: 0")
    public Double getFemaleWeightMin() {
        return femaleWeightMin;
    }

    @NotNull(message = "Campo Obrigatório")
    @Min(value = 0, message = "Campo deve ter valor maior que: 0")
    public Double getFemaleWeightMax() {
        return femaleWeightMax;
    }

    @NotNull(message = "Campo Obrigatório")
    public Boolean getHypoallergenic() {
        return hypoallergenic;
    }

    @NotBlank(message = "Campo Obrigatório")
    public String getSize() {
        return size;
    }

    public void setName(String name) {
        this.name = name;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public void setLifeExpectancyMin(Integer lifeExpectancyMin) {
        this.lifeExpectancyMin = lifeExpectancyMin;
    }

    public void setLifeExpectancyMax(Integer lifeExpectancyMax) {
        this.lifeExpectancyMax = lifeExpectancyMax;
    }

    public void setMaleWeightMin(Double maleWeightMin) {
        this.maleWeightMin = maleWeightMin;
    }

    public void setMaleWeightMax(Double maleWeightMax) {
        this.maleWeightMax = maleWeightMax;
    }

    public void setFemaleWeightMin(Double femaleWeightMin) {
        this.femaleWeightMin = femaleWeightMin;
    }

    public void setFemaleWeightMax(Double femaleWeightMax) {
        this.femaleWeightMax = femaleWeightMax;
    }

    public void setHypoallergenic(Boolean hypoallergenic) {
        this.hypoallergenic = hypoallergenic;
    }

    public void setSize(String size) {
        this.size = size;
    }

}

