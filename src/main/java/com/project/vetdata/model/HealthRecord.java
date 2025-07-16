package com.project.vetdata.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.project.vetdata.enums.DogSize;
import com.project.vetdata.enums.Euthanasia;
import com.project.vetdata.enums.Gender;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@Entity
@Table(name = "health_record")
public class HealthRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer age;

    @Column(name = "cod_patient")
    private String codPatient;

    private String color;

    private Boolean death;

    @Enumerated(EnumType.STRING)
    private Euthanasia euthanasia;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String patient;

    @Enumerated(EnumType.STRING)
    private DogSize size;

    private String tutor;

    private Double weight;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "breed_id", nullable = false)
    private DogBreed breed;

    public HealthRecord() {
    }

    public HealthRecord(Long id, Integer age, String codPatient, String color, boolean death,
                        Euthanasia euthanasia, Gender gender, String patient, DogSize size, String tutor,
                        Double weight, DogBreed breed) {
        this.id = id;
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
        this.breed = breed;
    }

    public Long getId() {
        return id;
    }


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

    public Euthanasia getEuthanasia() {
        return euthanasia;
    }

    public Gender getGender() {
        return gender;
    }

    public String getPatient() {
        return patient;
    }

    public DogSize getSize() {
        return size;
    }

    public String getTutor() {
        return tutor;
    }

    public Double getWeight() {
        return weight;
    }

    public DogBreed getBreed() {
        return breed;
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

    public void setBreed(DogBreed breed) {
        this.breed = breed;
    }
}
