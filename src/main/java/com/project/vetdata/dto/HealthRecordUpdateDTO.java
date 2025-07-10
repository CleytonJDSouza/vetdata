package com.project.vetdata.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.project.vetdata.enums.DogSize;
import com.project.vetdata.enums.Euthanasia;
import com.project.vetdata.enums.Gender;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class HealthRecordUpdateDTO {

    private Integer age;
    private String codPatient;
    private String color;
    private Boolean death;
    private Euthanasia euthanasia;
    private Gender gender;
    private String patient;
    private DogSize size;
    private String tutor;
    private Double weight;
    private Long breedId;

    public HealthRecordUpdateDTO() {
    }

    public HealthRecordUpdateDTO(Integer age, String codPatient, String color, Boolean death, Euthanasia euthanasia, Gender gender, String patient,
                                 DogSize size, String tutor, Double weight, Long breedId) {
        this.age = age;
        this.codPatient = codPatient;
        this.color = color;
        this.death = death;
        this.euthanasia = euthanasia;
        this.gender = gender;
        this.patient = patient;
        this.size = size;
        this.tutor = tutor;
        this.weight = weight;
        this.breedId = breedId;
    }

    @Min(value = 0, message = "Campo deve ser maior que 0")
    public Integer getAge() {
        return age;
    }

    public String getCodPatient() {
        return codPatient;
    }

    public String getColor() {
        return color;
    }

    public Boolean getDeath() {
        return death;
    }

    @NotNull(message = "Campo Obrigatório")
    public Euthanasia getEuthanasia() {
        return euthanasia;
    }

    @NotNull(message = "Campo Obrigatório")
    public Gender getGender() {
        return gender;
    }

    @NotBlank(message = "Campo Obrigatório")
    public String getPatient() {
        return patient;
    }

    @NotNull(message = "Campo Obrigatório")
    public DogSize getSize() {
        return size;
    }

    @NotBlank(message = "Campo Obrigatório")
    public String getTutor() {
        return tutor;
    }

    @Min(value = 0, message = "Campo deve ter valor maior que 0")
    public Double getWeight() {
        return weight;
    }

    @NotNull(message = "Campo Obrigatório")
    public Long getBreedId() {
        return breedId;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public void setCodPatient(String codPatient) {
        this.codPatient = codPatient;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setDeath(Boolean death) {
        this.death = death;
    }

    public void setEuthanasia(Euthanasia euthanasia) {
        this.euthanasia = euthanasia;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public void setPatient(String patient) {
        this.patient = patient;
    }

    public void setSize(DogSize size) {
        this.size = size;
    }

    public void setTutor(String tutor) {
        this.tutor = tutor;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public void setBreedId(Long breedId) {
        this.breedId = breedId;
    }
}
