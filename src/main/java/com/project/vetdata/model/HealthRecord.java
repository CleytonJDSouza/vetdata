package com.project.vetdata.model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "health_record")
public class HealthRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tutor;

    private String codPatient;

    private String patient;

    private Double age;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @ManyToOne
    @JoinColumn(name = "breed_id")
    private DogBreed breed;

    @Enumerated(EnumType.STRING)
    private DogSize size;

    private String color;

    private Double weight;

    private LocalDate admission;

    @Enumerated(EnumType.STRING)
    private Euthanasia euthanasia;

    private Boolean death;

    public HealthRecord(){
    }

    public HealthRecord(String tutor, String codPatient, String patient, Double age, Gender gender, DogBreed breed, DogSize size, String color, Double weight,
                        LocalDate admission, Euthanasia euthanasia, Boolean death) {
        this.tutor = tutor;
        this.codPatient = codPatient;
        this.patient = patient;
        this.age = age;
        this.gender = gender;
        this.breed = breed;
        this.size = size;
        this.color = color;
        this.weight = weight;
        this.admission = admission;
        this.euthanasia = euthanasia;
        this.death = death;
    }

    public Long getId() {
        return id;
    }

    public String getTutor() {
        return tutor;
    }

    public void setTutor(String tutor) {
        this.tutor = tutor;
    }

    public String getCodPatient() {
        return codPatient;
    }

    public void setCodPatient(String codPatient) {
        this.codPatient = codPatient;
    }

    public String getPatient() {
        return patient;
    }

    public void setPatient(String patient) {
        this.patient = patient;
    }

    public Double getAge() {
        return age;
    }

    public void setAge(Double age) {
        this.age = age;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public DogBreed getBreed() {
        return breed;
    }

    public void setBreed(DogBreed breed) {
        this.breed = breed;
    }

    public DogSize getSize() {
        return size;
    }

    public void setSize(DogSize size) {
        this.size = size;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public LocalDate getAdmission() {
        return admission;
    }

    public void setAdmission(LocalDate admission) {
        this.admission = admission;
    }

    public Euthanasia getEuthanasia() {
        return euthanasia;
    }

    public void setEuthanasia(Euthanasia euthanasia) {
        this.euthanasia = euthanasia;
    }

    public Boolean getDeath() {
        return death;
    }

    public void setDeath(Boolean death) {
        this.death = death;
    }
}
