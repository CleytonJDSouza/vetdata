package com.project.vetdata.dto;

import com.project.vetdata.enums.DogSize;
import com.project.vetdata.enums.Euthanasia;
import com.project.vetdata.enums.Gender;
import com.project.vetdata.model.DogBreed;
import com.project.vetdata.model.HealthRecord;

import java.time.LocalDate;

public class HealthRecordResponseDTO {

    private Long id;
    private String codPatient;
    private String tutor;
    private String patient;
    private String color;
    private Double age;
    private Double weight;
    private LocalDate admission;
    private DogSize size;
    private Gender gender;
    private Boolean death;
    private Euthanasia euthanasia;
    private Long breedId;
    private String breedName;

    public  HealthRecordResponseDTO() {

    }

    public HealthRecordResponseDTO(HealthRecord entity) {
        this.id = entity.getId();
        this.codPatient = entity.getCodPatient();
        this.tutor = entity.getTutor();
        this.patient = entity.getPatient();
        this.color = entity.getColor();
        this.age = entity.getAge();
        this.weight = entity.getWeight();
        this.admission = entity.getAdmission();
        this.size = entity.getSize();
        this.gender = entity.getGender();
        this.death = entity.getDeath();
        this.euthanasia = entity.getEuthanasia();
        this.breedId = entity.getBreed().getId();
        this.breedName = entity.getBreed().getName();
    }



    public Long getId() {
        return id;
    }

    public String getCodPatient() {
        return codPatient;
    }

    public String getTutor() {
        return tutor;
    }

    public String getPatient() {
        return patient;
    }

    public String getColor() {
        return color;
    }

    public Double getAge() {
        return age;
    }

    public Double getWeight() {
        return weight;
    }

    public LocalDate getAdmission() {
        return admission;
    }

    public DogSize getSize() {
        return size;
    }

    public Gender getGender() {
        return gender;
    }

    public Boolean getDeath() {
        return death;
    }

    public Euthanasia getEuthanasia() {
        return euthanasia;
    }

    public Long getBreedId() {
        return breedId;
    }

    public String getBreedName() {
        return breedName;
    }
}
