package com.project.vetdata.service;

import com.project.vetdata.dto.HospitalAdmissionResponseDTO;
import com.project.vetdata.model.Diagnostic;
import com.project.vetdata.dto.HospitalAdmissionCreateDTO;
import com.project.vetdata.model.HealthRecord;
import com.project.vetdata.model.HospitalAdmission;
import com.project.vetdata.model.PostOperative;
import com.project.vetdata.repository.DiagnosticRepository;
import com.project.vetdata.repository.HealthRecordRepository;
import com.project.vetdata.repository.HospitalAdmissionRepository;
import com.project.vetdata.repository.PostOperativeRepository;
import org.springframework.stereotype.Service;


import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class HospitalAdmissionServiceImpl implements HospitalAdmissionService {

    private final HospitalAdmissionRepository hospitalAdmissionRepository;
    private final HealthRecordRepository healthRecordRepository;
    private final DiagnosticRepository diagnosticRepository;
    private final PostOperativeRepository postOperativeRepository;

    public HospitalAdmissionServiceImpl (HospitalAdmissionRepository hospitalAdmissionRepository,HealthRecordRepository healthRecordRepository, DiagnosticRepository diagnosticRepository,
                                         PostOperativeRepository postOperativeRepository) {
        this.hospitalAdmissionRepository = hospitalAdmissionRepository;
        this.healthRecordRepository = healthRecordRepository;
        this.diagnosticRepository = diagnosticRepository;
        this.postOperativeRepository = postOperativeRepository;
    }

    @Override
    public HospitalAdmissionResponseDTO createHospitalAdmission(HospitalAdmissionCreateDTO dto) {
        HospitalAdmission admission = fromCreateDTO(dto);
        HospitalAdmission saved = hospitalAdmissionRepository.save(admission);
        return mapToResponse(saved);
    }

    private HospitalAdmission fromCreateDTO(HospitalAdmissionCreateDTO dto) {
        HealthRecord healthRecord = healthRecordRepository.findById(dto.getHealthRecordId())
                .orElseThrow(() -> new IllegalArgumentException("Prontuário com ID " + dto.getHealthRecordId() + " não encontrado"));

        Set<PostOperative> postOperatives = new HashSet<>();
        if (dto.getPostOperativeIds() != null && !dto.getPostOperativeIds().isEmpty()) {
            postOperatives = new HashSet<>(postOperativeRepository.findAllById(dto.getPostOperativeIds()));
            if (postOperatives.size() != dto.getPostOperativeIds().size()) {
                throw new IllegalArgumentException("Pós Operatório não encontrado");
            }
        }

        Set<Diagnostic> diagnostics = new HashSet<>();
        if (dto.getDiagnosticIds() != null && !dto.getDiagnosticIds().isEmpty()) {
            diagnostics = new HashSet<>(diagnosticRepository.findAllById(dto.getDiagnosticIds()));
            if (diagnostics.size() != dto.getDiagnosticIds().size()) {
                throw new IllegalArgumentException("Diagnóstico não encontrado");
            }
        }

        HospitalAdmission admission = new HospitalAdmission();
        admission.setDate(dto.getDate());
        admission.setReasonHospitalization(dto.getReasonHospitalization());
        admission.setDateMedicalDischarge(dto.getDateMedicalDischarge());
        admission.setDateReturns(dto.getDateReturns());
        admission.setMedicalEvolution(dto.getMedicalEvolution());
        admission.setTreatmentNextSteps(dto.getTreatmentNextSteps());
        admission.setHealthRecord(healthRecord);
        admission.setPostOperatives(postOperatives);
        admission.setDiagnostics(diagnostics);

        return admission;
    }
        private HospitalAdmissionResponseDTO mapToResponse (HospitalAdmission admission){
            return new HospitalAdmissionResponseDTO(
                    admission.getId(),
                    admission.getDate(),
                    admission.getReasonHospitalization(),
                    admission.getDateMedicalDischarge(),
                    admission.getDateReturns(),
                    admission.getMedicalEvolution(),
                    admission.getTreatmentNextSteps(),
                    admission.getHealthRecord() != null ? admission.getHealthRecord().getId() : null,
                    admission.getDiagnostics() != null ?
                            admission.getDiagnostics().stream()
                                    .map(Diagnostic::getId)
                                    .collect(Collectors.toSet())
                            : new HashSet<>(),
                    admission.getPostOperatives() != null ?
                            admission.getPostOperatives().stream()
                                    .map(PostOperative::getId)
                                    .collect(Collectors.toSet())
                            : new HashSet<>()
            );
        }
}
