package com.project.vetdata.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "dog_breeds")
public class DogBreed {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private int lifeExpectancyMin;
    private int lifeExpectancyMax;
    private double maleWeightMin;
    private double maleWeightMax;
    private double femaleWeightMin;
    private double femaleWeightMax;
    private boolean hypoallergenic;
    private String size;
}
