package com.project.vetdata.dto;

import com.project.vetdata.model.Diagnostic;
import com.project.vetdata.model.HospitalAdmission;
import com.project.vetdata.model.PostOperative;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

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

    public HospitalAdmissionResponseDTO(HospitalAdmission admission) {
        this.id = admission.getId();
        this.date = admission.getDate();
        this.reasonHospitalization = admission.getReasonHospitalization();
        this.dateMedicalDischarge = admission.getDateMedicalDischarge();
        this.dateReturns = admission.getDateReturns();
        this.medicalEvolution = admission.getMedicalEvolution() != null ? admission.getMedicalEvolution() : null;
        this.treatmentNextSteps = admission.getTreatmentNextSteps();
        this.healthRecordId = admission.getHealthRecord() != null ? admission.getHealthRecord().getId() : null;
        this.diagnosticIds = admission.getDiagnostics() != null
                ? admission.getDiagnostics().stream()
                .map(Diagnostic::getId)
                .collect(Collectors.toSet())
                : Collections.emptySet();

        this.postOperativeIds = admission.getPostOperatives() != null
                ? admission.getPostOperatives().stream()
                .map(PostOperative::getId)
                .collect(Collectors.toSet())
                : Collections.emptySet();
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
