package com.project.vetdata.dto;

public class DogBreedDTO {

    private Long id;
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

    public DogBreedDTO(){}

    public DogBreedDTO(Long id ,String name, String description, Integer lifeExpectancyMin, Integer lifeExpectancyMax, Double maleWeightMin, Double maleWeightMax,
                            Double femaleWeightMin, Double femaleWeightMax, Boolean hypoallergenic, String size) {
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
}
