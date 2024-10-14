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

    @Column(name = "id_external_api")
    private String idExternalApi;

    @NotBlank
    private String name;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String description;

    @NotNull
    @Min(0)
    private Integer lifeExpectancyMin;

    @NotNull
    @Min(0)
    private Integer lifeExpectancyMax;

    @NotNull
    @Min(0)
    private Double maleWeightMin;

    @NotNull
    @Min(0)
    private Double maleWeightMax;

    @NotNull
    @Min(0)
    private Double femaleWeightMin;

    @NotNull
    @Min(0)
    private Double femaleWeightMax;

    @NotNull
    private Boolean hypoallergenic;

    private String size;

    public DogBreed() {
    }

    public DogBreed(Long id, String idExternalApi ,String name, String description, Integer lifeExpectancyMin, Integer lifeExpectancyMax, Double maleWeightMin, Double maleWeightMax, Double femaleWeightMin,
                    Double femaleWeightMax, Boolean hypoallergenic, String size) {
        this.id = id;
        this.idExternalApi = idExternalApi;
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

    public String getIdExternalApi() {
        return idExternalApi;
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

    public void setId(Long id) {
        this.id = id;
    }

    public String getSize() {
        return size;
    }

    public void setIdExternalApi(String idExternalApi) {
        this.idExternalApi = idExternalApi;
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


