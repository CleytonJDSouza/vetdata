package com.project.vetdata.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
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

    @NotBlank(message = "Campo Obrigatório")
    private String name;

    private String description;

    @Min(value = 0, message = "Campo deve ter valor maior que: 0")
    private int lifeExpectancyMin;

    @Min(value = 0, message = "Campo deve ter valor maior que: 0")
    private int lifeExpectancyMax;

    @Min(value = 0, message = "Campo deve ter valor maior que: 0")
    private double maleWeightMin;

    @Min(value = 0, message = "Campo deve ter valor maior que: 0")
    private double maleWeightMax;

    @Min(value = 0, message = "Campo deve ter valor maior que: 0")
    private double femaleWeightMin;

    @Min(value = 0, message = "Campo deve ter valor maior que: 0")
    private double femaleWeightMax;

    private boolean hypoallergenic;

    @NotBlank(message = "Campo Obrigatório")
    private String size;
}
