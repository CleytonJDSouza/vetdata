package com.project.vetdata.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "dog_breeds")
public class DogBreed {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Campo Obrigatório")
    private String name;

    private String description;

    @NotNull(message = "Campo Obrigatório")
    @Min(value = 0, message = "Campo deve ter valor maior que: 0")
    private Integer lifeExpectancyMin;

    @NotNull(message = "Campo Obrigatório")
    @Min(value = 0, message = "Campo deve ter valor maior que: 0")
    private Integer lifeExpectancyMax;

    @NotNull(message = "Campo Obrigatório")
    @Min(value = 0, message = "Campo deve ter valor maior que: 0")
    private Double maleWeightMin;

    @NotNull(message = "Campo Obrigatório")
    @Min(value = 0, message = "Campo deve ter valor maior que: 0")
    private Double maleWeightMax;

    @NotNull(message = "Campo Obrigatório")
    @Min(value = 0, message = "Campo deve ter valor maior que: 0")
    private Double femaleWeightMin;

    @NotNull(message = "Campo Obrigatório")
    @Min(value = 0, message = "Campo deve ter valor maior que: 0")
    private Double femaleWeightMax;

    @NotNull(message = "Campo Obrigatório")
    private Boolean hypoallergenic;

    @NotBlank(message = "Campo Obrigatório")
    private String size;

    public DogBreed() {
    }

    public DogBreed(Long id, String name, String description, Integer lifeExpectancyMin, Integer lifeExpectancyMax, Double maleWeightMin, Double maleWeightMax, Double femaleWeightMin,
                    Double femaleWeightMax, Boolean hypoallergenic, String size) {
        this.id = id;
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

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Integer getLifeExpectancyMin() {
        return lifeExpectancyMin;
    }

    public Integer getLifeExpectancyMax() {
        return lifeExpectancyMax;
    }

    public Double getMaleWeightMin() {
        return maleWeightMin;
    }

    public Double getMaleWeightMax() {
        return maleWeightMax;
    }

    public Double getFemaleWeightMin() {
        return femaleWeightMin;
    }

    public Double getFemaleWeightMax() {
        return femaleWeightMax;
    }

    public Boolean getHypoallergenic() {
        return hypoallergenic;
    }

    public String getSize() {
        return size;
    }
}


