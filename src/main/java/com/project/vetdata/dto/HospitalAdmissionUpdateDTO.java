package com.project.vetdata.dto;

import com.project.vetdata.enums.MedicalEvolution;
import com.project.vetdata.enums.ReasonHospitalization;
import com.project.vetdata.enums.TreatmentNextSteps;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.Set;

public class HospitalAdmissionUpdateDTO {

    @NotNull(message = "Campo Obrigatório")
    private LocalDate date;

    @NotNull(message = "Campo Obrigatório")
    private ReasonHospitalization reasonHospitalization;

    private LocalDate dateMedicalDischarge;

    private LocalDate dateReturns;

    @NotNull(message = "Campo Obrigatório")
    private MedicalEvolution medicalEvolution;

    @NotNull(message = "Campo Obrigatório")
    private TreatmentNextSteps treatmentNextSteps;

    @NotNull(message = "Campo Obrigatório")
    private Long healthRecordId;

    @NotNull(message = "Campo Obrigatório")
    private Set<Long> postOperativeIds;

    @NotNull(message = "Campo Obrigatório")
    private Set<Long> diagnosticIds;

    public HospitalAdmissionUpdateDTO() {
    }

    public HospitalAdmissionUpdateDTO(Long id, LocalDate date, ReasonHospitalization reasonHospitalization, LocalDate dateMedicalDischarge, LocalDate dateReturns,
                                      MedicalEvolution medicalEvolution, TreatmentNextSteps treatmentNextSteps, Long healthRecordId, Set<Long> postOperativeIds, Set<Long> diagnosticIds) {
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

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public ReasonHospitalization getReasonHospitalization() {
        return reasonHospitalization;
    }

    public void setReasonHospitalization(ReasonHospitalization reasonHospitalization) {
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

    public MedicalEvolution getMedicalEvolution() {
        return medicalEvolution;
    }

    public void setMedicalEvolution(MedicalEvolution medicalEvolution) {
        this.medicalEvolution = medicalEvolution;
    }

    public TreatmentNextSteps getTreatmentNextSteps() {
        return treatmentNextSteps;
    }

    public void setTreatmentNextSteps(TreatmentNextSteps treatmentNextSteps) {
        this.treatmentNextSteps = treatmentNextSteps;
    }

    public Long getHealthRecordId() {
        return healthRecordId;
    }

    public void setHealthRecordId(Long healthRecordId) {
        this.healthRecordId = healthRecordId;
    }

    public Set<Long> getPostOperativeIds() {
        return postOperativeIds;
    }

    public void setPostOperativeIds(Set<Long> postOperativeIds) {
        this.postOperativeIds = postOperativeIds;
    }

    public Set<Long> getDiagnosticIds() {
        return diagnosticIds;
    }

    public void setDiagnosticIds(Set<Long> diagnosticIds) {
        this.diagnosticIds = diagnosticIds;
    }
}

