package com.project.vetdata.service;

import com.project.vetdata.dto.HospitalAdmissionCreateDTO;
import com.project.vetdata.dto.HospitalAdmissionResponseDTO;
import com.project.vetdata.enums.DogSize;
import com.project.vetdata.enums.Euthanasia;
import com.project.vetdata.enums.Gender;
import com.project.vetdata.model.*;
import com.project.vetdata.repository.DiagnosticRepository;
import com.project.vetdata.repository.HealthRecordRepository;
import com.project.vetdata.repository.HospitalAdmissionRepository;
import com.project.vetdata.repository.PostOperativeRepository;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class HospitalAdmissionServiceImplTest {

    @InjectMocks
    private  HospitalAdmissionServiceImpl hospitalAdmissionService;

    @Mock
    private HospitalAdmissionRepository hospitalAdmissionRepository;

    @Mock
    private HealthRecordRepository healthRecordRepository;

    @Mock
    private DiagnosticRepository diagnosticRepository;

    @Mock
    private PostOperativeRepository postOperativeRepository;

    @Test
    public void given_valid_dto_when_createHospitalAdmission_then_admission_is_saved() {
        HospitalAdmissionCreateDTO dto = getFakeHospitalAdmissionDTO();
        HealthRecord healthRecord = getFakeHealthRecord();
        Set<Diagnostic> diagnostics = Set.of(getFakeDiagnostic());
        Set<PostOperative> postOperatives = Set.of(getFakePostOperative());
        HospitalAdmission savedAdmission = getFakeHospitalAdmission();

        when(healthRecordRepository.findById(dto.getHealthRecordId())).thenReturn(Optional.of(healthRecord));
        when(diagnosticRepository.findAllById(dto.getDiagnosticIds())).thenReturn(List.of(getFakeDiagnostic()));
        when(postOperativeRepository.findAllById(dto.getPostOperativeIds())).thenReturn(List.of(getFakePostOperative()));
        when(hospitalAdmissionRepository.save(any(HospitalAdmission.class))).thenReturn(savedAdmission);

        HospitalAdmissionResponseDTO result = hospitalAdmissionService.createHospitalAdmission(dto);

        assertEquals(dto.getReasonHospitalization(), result.getReasonHospitalization());
        assertEquals(dto.getDate(), result.getDate());
        assertEquals(dto.getDateMedicalDischarge(), result.getDateMedicalDischarge());
        assertEquals(dto.getDateReturns(), result.getDateReturns());
    }

    @Test
    public void given_invalid_healthRecordId_when_createHospitalAdmission_then_throws_exception() {
        HospitalAdmissionCreateDTO dto = getFakeHospitalAdmissionDTO();

        when(healthRecordRepository.findById(dto.getHealthRecordId())).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> hospitalAdmissionService.createHospitalAdmission(dto));

        assertEquals("Prontuário com ID " + dto.getHealthRecordId() + " não encontrado", exception.getMessage());
    }

    @Test
    public void given_invalid_diagnosticId_when_createHospitalAdmission_then_exception() {
        HospitalAdmissionCreateDTO dto = getFakeHospitalAdmissionDTO();

        when(healthRecordRepository.findById(dto.getHealthRecordId())).thenReturn(Optional.of(getFakeHealthRecord()));
        when(postOperativeRepository.findAllById(dto.getPostOperativeIds())).thenReturn(List.of(getFakePostOperative()));
        when(diagnosticRepository.findAllById(dto.getDiagnosticIds())).thenReturn(Collections.emptyList());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> hospitalAdmissionService.createHospitalAdmission(dto));

        assertEquals("Diagnóstico não encontrado", exception.getMessage());
    }

    @Test
    public void given_invalid_postOperativeId_when_createHospitalAdmission_then_throws_exception() {
        HospitalAdmissionCreateDTO dto = getFakeHospitalAdmissionDTO();

        when(healthRecordRepository.findById(dto.getHealthRecordId())).thenReturn(Optional.of(getFakeHealthRecord()));
        when(postOperativeRepository.findAllById(dto.getPostOperativeIds())).thenReturn(Collections.emptyList());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                hospitalAdmissionService.createHospitalAdmission(dto)
        );

        assertEquals("Pós Operatório não encontrado", exception.getMessage());
    }

    private HospitalAdmissionCreateDTO getFakeHospitalAdmissionDTO() {
        HospitalAdmissionCreateDTO dto = new HospitalAdmissionCreateDTO();
        dto.setDate(LocalDate.of(2025, 4, 24));
        dto.setReasonHospitalization("Cirurgia");
        dto.setDateMedicalDischarge(LocalDate.of(2025, 4, 27));
        dto.setDateReturns(LocalDate.of(2025, 5, 10));
        dto.setMedicalEvolution("Boa");
        dto.setTreatmentNextSteps("Reabilitação");
        dto.setHealthRecordId(1L);
        dto.setDiagnosticIds(Set.of(1L));
        dto.setPostOperativeIds(Set.of(1L));
        return dto;
    }

    private HospitalAdmission getFakeHospitalAdmission() {
        HospitalAdmission ha = new HospitalAdmission();
        ha.setReasonHospitalization("Cirurgia");
        ha.setDate(LocalDate.of(2025, 4, 24));
        ha.setDateMedicalDischarge(LocalDate.of(2025, 4, 27));
        ha.setDateReturns(LocalDate.of(2025, 5, 10));
        ha.setMedicalEvolution("Boa");
        ha.setTreatmentNextSteps("Reabilitação");
        return ha;
    }
    private HealthRecord getFakeHealthRecord() {
        HealthRecord record = new HealthRecord();
        record.setCodPatient("P01");
        record.setTutor("Cleyton");
        record.setPatient("Torresmo");
        record.setBreed(getFakeDogBreed());
        record.setAge(5.0);
        record.setWeight(22.0);
        record.setColor("Bege");
        record.setSize(DogSize.MEDIUM);
        record.setGender(Gender.MALE);
        record.setDeath(null);
        record.setEuthanasia(Euthanasia.NO);
        record.setAdmission(LocalDate.of(2025, 1, 1));

        return record;
    }


    public DogBreed getFakeDogBreed() {
        return new DogBreed(1L, "2", "Golden Retriever", "Amigável e inteligente e esperto",
                10, 12, 30D, 34D, 25D, 29D,
                false, "Medio");
    }

    private Diagnostic getFakeDiagnostic() {
        Diagnostic diagnostic = new Diagnostic();
        diagnostic.setDescription("Cancer");
        diagnostic.setObservation("test");
        return diagnostic;
    }

    private PostOperative getFakePostOperative() {
        PostOperative postOperative = new PostOperative();
        postOperative.setDescription("Fisioterapia");
        return postOperative;
    }
}
