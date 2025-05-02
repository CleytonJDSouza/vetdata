package com.project.vetdata.dto;

import java.time.LocalDate;
import java.util.Set;

public class HospitalAdmissionResponseDTO {

    private Long id;
    private LocalDate date;
    private String reasonHospitalization;
    private LocalDate dateMedicalDischarge;
    private LocalDate dateReturns;
    private String medicalEvolution;
    private String treatmentNextSteps;
    private Long healthRecordId;
    private Set<Long> diagnosticIds;
    private Set<Long> postOperativeIds;

    public HospitalAdmissionResponseDTO() {
    }

    public HospitalAdmissionResponseDTO(Long id, LocalDate date, String reasonHospitalization, LocalDate dateMedicalDischarge, LocalDate dateReturns,
                                        String medicalEvolution, String treatmentNextSteps, Long healthRecordId, Set<Long> diagnosticIds, Set<Long> postOperativeIds) {
        this.id = id;
        this.date = date;
        this.reasonHospitalization = reasonHospitalization;
        this.dateMedicalDischarge = dateMedicalDischarge;
        this.dateReturns = dateReturns;
        this.medicalEvolution = medicalEvolution;
        this.treatmentNextSteps = treatmentNextSteps;
        this.healthRecordId = healthRecordId;
        this.diagnosticIds = diagnosticIds;
        this.postOperativeIds = postOperativeIds;
    }

    public Long getId() {
        return id;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getReasonHospitalization() {
        return reasonHospitalization;
    }

    public LocalDate getDateMedicalDischarge() {
        return dateMedicalDischarge;
    }

    public LocalDate getDateReturns() {
        return dateReturns;
    }

    public String getMedicalEvolution() {
        return medicalEvolution;
    }

    public String getTreatmentNextSteps() {
        return treatmentNextSteps;
    }

    public Long getHealthRecordId() {
        return healthRecordId;
    }

    public Set<Long> getDiagnosticIds() {
        return diagnosticIds;
    }

    public Set<Long> getPostOperativeIds() {
        return postOperativeIds;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setTreatmentNextSteps(String treatmentNextSteps) {
        this.treatmentNextSteps = treatmentNextSteps;
    }

    public void setMedicalEvolution(String medicalEvolution) {
        this.medicalEvolution = medicalEvolution;
    }

    public void setReasonHospitalization(String reasonHospitalization) {
        this.reasonHospitalization = reasonHospitalization;
    }

    public void setDateMedicalDischarge(LocalDate dateMedicalDischarge) {
        this.dateMedicalDischarge = dateMedicalDischarge;
    }

    public void setDateReturns(LocalDate dateReturns) {
        this.dateReturns = dateReturns;
    }

    public void setDiagnosticIds(Set<Long> diagnosticIds) {
        this.diagnosticIds = diagnosticIds;
    }

    public void setHealthRecordId(Long healthRecordId) {
        this.healthRecordId = healthRecordId;
    }

    public void setPostOperativeIds(Set<Long> postOperativeIds) {
        this.postOperativeIds = postOperativeIds;
    }

}
