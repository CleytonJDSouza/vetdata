package com.project.vetdata.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.Set;

public class HospitalAdmissionCreateDTO {

    private LocalDate date;
    private String reasonHospitalization;
    private LocalDate dateMedicalDischarge;
    private LocalDate dateReturns;
    private String medicalEvolution;
    private String treatmentNextSteps;
    private Long healthRecordId;
    private Set<Long> postOperativeIds;
    private Set<Long> diagnosticIds;

    public HospitalAdmissionCreateDTO() {
    }

    public HospitalAdmissionCreateDTO(LocalDate date, String reasonHospitalization, LocalDate dateMedicalDischarge, LocalDate dateReturns, String medicalEvolution,
                                      String treatmentNextSteps, Long healthRecordId, Set<Long> postOperativeIds, Set<Long> diagnosticIds) {
        this.date = date;
        this.reasonHospitalization = reasonHospitalization;
        this.dateMedicalDischarge = dateMedicalDischarge;
        this.dateReturns = dateReturns;
        this.medicalEvolution = medicalEvolution;
        this.treatmentNextSteps = treatmentNextSteps;
        this.healthRecordId = healthRecordId;
        this.postOperativeIds = postOperativeIds;
        this.diagnosticIds = diagnosticIds;
    }

    @NotNull(message = "Data da internação é obrigatória")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    public LocalDate getDate() {
        return date;
    }

    @NotBlank(message = "Motivo da internação é obrigatório")
    public String getReasonHospitalization() {
        return reasonHospitalization;
    }

    @NotNull(message = "Data de alta médica é obrigatória")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    public LocalDate getDateMedicalDischarge() {
        return dateMedicalDischarge;
    }

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    public LocalDate getDateReturns() {
        return dateReturns;
    }

    public String getMedicalEvolution() {
        return medicalEvolution;
    }

    public String getTreatmentNextSteps() {
        return treatmentNextSteps;
    }

    @NotNull(message = "ID do prontuário é obrigatório")
    public Long getHealthRecordId() {
        return healthRecordId;
    }

    public Set<Long> getPostOperativeIds() {
        return postOperativeIds;
    }

    public Set<Long> getDiagnosticIds() {
        return diagnosticIds;
    }

    public void setDate(LocalDate date) {
        this.date = date;
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

    public void setMedicalEvolution(String medicalEvolution) {
        this.medicalEvolution = medicalEvolution;
    }

    public void setTreatmentNextSteps(String treatmentNextSteps) {
        this.treatmentNextSteps = treatmentNextSteps;
    }

    public void setHealthRecordId(Long healthRecordId) {
        this.healthRecordId = healthRecordId;
    }

    public void setPostOperativeIds(Set<Long> postOperativeIds) {
        this.postOperativeIds = postOperativeIds;
    }

    public void setDiagnosticIds(Set<Long> diagnosticIds) {
        this.diagnosticIds = diagnosticIds;
    }
}
