package com.project.vetdata.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name = "hospital_admission")
public class HospitalAdmission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;

    private String reasonHospitalization;

    private LocalDate dateMedicalDischarge;

    @Enumerated(EnumType.STRING)
    private ReasonMedicalDischarge reasonMedicalDischarge;

    private LocalDate dateReturn;

    @Enumerated(EnumType.STRING)
    private MedicalEvolution medicalEvolution;

    private String treatmentNextSteps;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinTable(name = "health_record_id")
    private HealthRecord healthRecord;

    public HospitalAdmission(){
    }

    public HospitalAdmission(LocalDate date, String reasonHospitalization, LocalDate dateMedicalDischarge, LocalDate dateReturn, MedicalEvolution medicalEvolution,
                             String treatmentNextSteps, HealthRecord healthRecord) {
        this.date = date;
        this.reasonHospitalization = reasonHospitalization;
        this.dateMedicalDischarge = dateMedicalDischarge;
        this.dateReturn = dateReturn;
        this.medicalEvolution = medicalEvolution;
        this.treatmentNextSteps = treatmentNextSteps;
        this.healthRecord = healthRecord;
    }

    public Long getId() {
        return id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getReasonHospitalization() {
        return reasonHospitalization;
    }

    public void setReasonHospitalization(String reasonHospitalization) {
        this.reasonHospitalization = reasonHospitalization;
    }

    public LocalDate getDateMedicalDischarge() {
        return dateMedicalDischarge;
    }

    public void setDateMedicalDischarge(LocalDate dateMedicalDischarge) {
        this.dateMedicalDischarge = dateMedicalDischarge;
    }

    public LocalDate getDateReturn() {
        return dateReturn;
    }

    public void setDateReturn(LocalDate dateReturn) {
        this.dateReturn = dateReturn;
    }

    public MedicalEvolution getMedicalEvolution() {
        return medicalEvolution;
    }

    public void setMedicalEvolution(MedicalEvolution medicalEvolution) {
        this.medicalEvolution = medicalEvolution;
    }

    public String getTreatmentNextSteps() {
        return treatmentNextSteps;
    }

    public void setTreatmentNextSteps(String treatmentNextSteps) {
        this.treatmentNextSteps = treatmentNextSteps;
    }

    public HealthRecord getHealthRecord() {
        return healthRecord;
    }

    public void setHealthRecord(HealthRecord healthRecord) {
        this.healthRecord = healthRecord;
    }
}


