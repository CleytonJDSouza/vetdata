package com.project.vetdata.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DogBreed {

    private String name;
    private String description;
    private int lifeExpectancyMin;
    private int lifeExpectancyMax;
    private double maleWeightmin;
    private double maleWeightmax;
    private double femaleWeightmin;
    private double femaleWeightmax;
    private boolean hypoallergenic;
    private String size;
}
