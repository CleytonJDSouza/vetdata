package com.project.vetdata.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "hospital_admission")
public class HospitalAdmission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;

    @Column(name = "reason_hospitalization", columnDefinition = "TEXT")
    private String reasonHospitalization;

    @Column(name = "date_medical_discharge")
    private LocalDate dateMedicalDischarge;

    @Column(name = "date_return")
    private LocalDate dateReturns;

    @Column(name = "medical_evolution")
    private String medicalEvolution;

    @Column(name = "treatment_next_steps", columnDefinition = "TEXT")
    private String treatmentNextSteps;

    @ManyToOne(optional = false)
    @JoinColumn(name = "health_record_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_health_record_id"))
    private HealthRecord healthRecord;

    @ManyToMany
    @JoinTable(name = "admission_post_operative",
            joinColumns = @JoinColumn(name = "id_hospital_admission"),
            inverseJoinColumns = @JoinColumn(name = "id_post_operative"))
    private Set<PostOperative> postOperatives = new HashSet<>();

    @ManyToMany
    @JoinTable(name = "admission_diagnostic",
            joinColumns = @JoinColumn(name = "id_hospital_admission"),
            inverseJoinColumns = @JoinColumn(name = "id_diagnostic"))
    private Set<Diagnostic> diagnostics = new HashSet<>();

    public HospitalAdmission() {
    }

    public HospitalAdmission(LocalDate date, String reasonHospitalization, LocalDate dateMedicalDischarge,
                             LocalDate dateReturns, String medicalEvolution, String treatmentNextSteps,
                             HealthRecord healthRecord, Set<PostOperative> postOperatives, Set<Diagnostic> diagnostics) {
        this.date = date;
        this.reasonHospitalization = reasonHospitalization;
        this.dateMedicalDischarge = dateMedicalDischarge;
        this.dateReturns = dateReturns;
        this.medicalEvolution = medicalEvolution;
        this.treatmentNextSteps = treatmentNextSteps;
        this.healthRecord = healthRecord;
        this.postOperatives = postOperatives;
        this.diagnostics = diagnostics;
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

    public LocalDate getDateReturns() {
        return dateReturns;
    }

    public void setDateReturns(LocalDate dateReturns) {
        this.dateReturns = dateReturns;
    }

    public String getMedicalEvolution() {
        return medicalEvolution;
    }

    public void setMedicalEvolution(String medicalEvolution) {
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

    public Set<PostOperative> getPostOperatives() {
        return postOperatives;
    }

    public void setPostOperatives(Set<PostOperative> postOperatives) {
        this.postOperatives = postOperatives;
    }

    public Set<Diagnostic> getDiagnostics() {
        return diagnostics;
    }

    public void setDiagnostics(Set<Diagnostic> diagnostics) {
        this.diagnostics = diagnostics;
    }
}
