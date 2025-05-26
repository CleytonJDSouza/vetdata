package com.project.vetdata.service;

import com.project.vetdata.dto.HospitalAdmissionCreateDTO;
import com.project.vetdata.dto.HospitalAdmissionResponseDTO;
import com.project.vetdata.dto.HospitalAdmissionUpdateDTO;
import com.project.vetdata.enums.*;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
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

    @Test
    public void given_valid_id_and_updateDTO_when_updateHospitalAdmission_then_admission_is_updated() {
        Long id = 1L;
        HospitalAdmissionUpdateDTO dto = new HospitalAdmissionUpdateDTO();
        dto.setReasonHospitalization(ReasonHospitalization.ENDOCRINOLOGIA);
        dto.setDate(LocalDate.of(2025, 7, 4));
        dto.setDateMedicalDischarge(LocalDate.of(2025, 9, 15));
        dto.setDateReturns(LocalDate.of(2025, 10, 15));
        dto.setMedicalEvolution(MedicalEvolution.PCR);
        dto.setTreatmentNextSteps(TreatmentNextSteps.ALTA_MEDICA);
        dto.setDiagnosticIds(Set.of(1L));
        dto.setPostOperativeIds(Set.of(1L));

        HospitalAdmission existingAdmission = getFakeHospitalAdmission();
        existingAdmission.setHealthRecord(getFakeHealthRecord());

        Diagnostic diagnostic = getFakeDiagnostic();
        PostOperative postOperative = getFakePostOperative();

        when(hospitalAdmissionRepository.findById(id)).thenReturn(Optional.of(existingAdmission));
        when(diagnosticRepository.findAllById(dto.getDiagnosticIds())).thenReturn(List.of(diagnostic));
        when(postOperativeRepository.findAllById(dto.getPostOperativeIds())).thenReturn(List.of(postOperative));
        when(hospitalAdmissionRepository.save(any(HospitalAdmission.class))).thenReturn(existingAdmission);

        HospitalAdmissionResponseDTO response = hospitalAdmissionService.updateHospitalAdmission(id, dto);

        verify(hospitalAdmissionRepository).findById(id);
        verify(hospitalAdmissionRepository).save(existingAdmission);

        assertEquals(dto.getReasonHospitalization(), response.getReasonHospitalization());
        assertEquals(dto.getDate(), response.getDate());
        assertEquals(dto.getDateMedicalDischarge(), response.getDateMedicalDischarge());
        assertEquals(dto.getDateReturns(), response.getDateReturns());
        assertEquals(dto.getMedicalEvolution(), response.getMedicalEvolution());
        assertEquals(dto.getTreatmentNextSteps(), response.getTreatmentNextSteps());
    }

    @Test
    public void given_invalid_id_when_updateHospitalAdmission_then_throws_exception() {
        Long invalidId = 99L;
        HospitalAdmissionUpdateDTO dto = new HospitalAdmissionUpdateDTO();
        dto.setDiagnosticIds(Set.of(1L));
        dto.setPostOperativeIds(Set.of(1L));

        when(hospitalAdmissionRepository.findById(invalidId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> hospitalAdmissionService.updateHospitalAdmission(invalidId, dto));

        assertEquals("Internação com ID " + invalidId + " não encontrada", exception.getMessage());
    }

    @Test
    public void given_invalid_diagnosticId_when_updateHospitalAdmission_then_throws_exception() {
        Long id = 1L;
        HospitalAdmissionUpdateDTO dto = new HospitalAdmissionUpdateDTO();
        dto.setDiagnosticIds(Set.of(99L));
        dto.setPostOperativeIds(Set.of(1L));

        HospitalAdmission existingAdmission = getFakeHospitalAdmission();
        existingAdmission.setHealthRecord(getFakeHealthRecord());

        when(hospitalAdmissionRepository.findById(id)).thenReturn(Optional.of(existingAdmission));
        when(diagnosticRepository.findAllById(dto.getDiagnosticIds())).thenReturn(Collections.emptyList());
        when(postOperativeRepository.findAllById(dto.getPostOperativeIds())).thenReturn(List.of(getFakePostOperative()));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> hospitalAdmissionService.updateHospitalAdmission(id, dto));

        assertEquals("Diagnóstico não foi encontrado", exception.getMessage());
    }

    @Test
    public void given_invalid_postOperativeId_when_updateHospitalAdmission_then_throws_exception() {
        Long id = 1L;
        HospitalAdmissionUpdateDTO dto = new HospitalAdmissionUpdateDTO();
        dto.setDiagnosticIds(Set.of(1L));
        dto.setPostOperativeIds(Set.of(88L));

        HospitalAdmission existingAdmission = getFakeHospitalAdmission();
        existingAdmission.setHealthRecord(getFakeHealthRecord());

        when(hospitalAdmissionRepository.findById(id)).thenReturn(Optional.of(existingAdmission));
        when(postOperativeRepository.findAllById(dto.getPostOperativeIds())).thenReturn(Collections.emptyList());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> hospitalAdmissionService.updateHospitalAdmission(id, dto));

        assertEquals("Pós Operatório não foi encontrado", exception.getMessage());
    }

    @Test
    public void given_existing_hospitalAdmission_when_getAll_then_returns_paginated_list() {
        HospitalAdmission admission = getFakeHospitalAdmission();
        List<HospitalAdmission> admissions = List.of(admission);

        when(hospitalAdmissionRepository.findAll()).thenReturn(admissions);

        List<HospitalAdmissionResponseDTO> result = hospitalAdmissionService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(admission.getReasonHospitalization(), result.get(0).getReasonHospitalization());

    }

    @Test
    public void given_no_hospitalAdmission_when_getAll_then_returns_empty_page() {
        when(hospitalAdmissionRepository.findAll()).thenReturn(List.of());

        List<HospitalAdmissionResponseDTO> result = hospitalAdmissionService.findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void given_valid_is_when_deleteHospitalAdmission_is_called_then_user_is_deleted() {
        Long hospitalAdmissionId = 1L;

        hospitalAdmissionService.deleteHospitalAdmission(hospitalAdmissionId);

        verify(hospitalAdmissionRepository, times(1)).deleteById(hospitalAdmissionId);
    }

    @Test
    public void given_invalid_id_when_deleteHospitalAdmission_the_no_exception_is_thrown_and_repository_is_called() {
        Long invalidHospitalAdmissionId = 999L;

        hospitalAdmissionService.deleteHospitalAdmission(invalidHospitalAdmissionId);

        verify(hospitalAdmissionRepository, times(1)).deleteById(invalidHospitalAdmissionId);
    }

    @Test
    public void given_valid_id_when_getHospitalAdmissionById_is_called_then_healthRecord_is_returned() {
        Long id = 1L;
        HospitalAdmission admission = getFakeHospitalAdmission();

        when(hospitalAdmissionRepository.findById(id)).thenReturn(Optional.of(admission));

        Optional<HospitalAdmission> result = hospitalAdmissionService.getHospitalAdmissionById(id);

        assertTrue(result.isPresent(), "A internação deve ser encontrada");
        assertEquals(admission.getTreatmentNextSteps(), result.get().getTreatmentNextSteps(), "O nome do tratamento deve ser igual");
    }

    @Test
    public void given_invalid_is_when_getHospitalAdmissionById_is_called_optional_empty_is_returned() {
        Long id = 99L;

        when(hospitalAdmissionRepository.findById(id)).thenReturn(Optional.empty());

        Optional<HospitalAdmission> result = hospitalAdmissionService.getHospitalAdmissionById(id);

        assertFalse(result.isPresent(), "A internação não deve econtrada");
    }

    @Test
    public void given_hospitalAdmission_when_HospitalAdmissionResponseDTO_constructor_called_then_fields_are_mapped_correctly() {
        HospitalAdmission admission = getFakeHospitalAdmission();
        admission.setHealthRecord(getFakeHealthRecord());
        admission.setDiagnostics(Set.of(getFakeDiagnostic()));
        admission.setPostOperatives(Set.of(getFakePostOperative()));

        HospitalAdmissionResponseDTO responseDTO = new HospitalAdmissionResponseDTO(admission);

        assertNotNull(responseDTO);
        assertEquals(admission.getId(), responseDTO.getId());
        assertEquals(admission.getDate(), responseDTO.getDate());
        assertEquals(admission.getReasonHospitalization(), responseDTO.getReasonHospitalization());
        assertEquals(admission.getDateMedicalDischarge(), responseDTO.getDateMedicalDischarge());
        assertEquals(admission.getDateReturns(), responseDTO.getDateReturns());
        assertEquals(admission.getMedicalEvolution(), responseDTO.getMedicalEvolution());
        assertEquals(admission.getTreatmentNextSteps(), responseDTO.getTreatmentNextSteps());
        assertEquals(admission.getHealthRecord().getId(), responseDTO.getHealthRecordId());
        assertEquals(1, responseDTO.getDiagnosticIds().size());
        assertEquals(1, responseDTO.getPostOperativeIds().size());
    }

    @Test
    void given_hospitalAdmission_with_null_fields_when_HospitalAdmissionResponseDTO_constructor_called_then_handle_nulls_correctly() {
        HospitalAdmission admission = new HospitalAdmission();
        admission.setDate(LocalDate.now());
        admission.setReasonHospitalization(ReasonHospitalization.NEFROLOGIA);
        admission.setDateMedicalDischarge(LocalDate.now().plusDays(10));
        admission.setDateReturns(LocalDate.now().plusDays(20));
        admission.setMedicalEvolution(null);
        admission.setTreatmentNextSteps(TreatmentNextSteps.CONTINUOU_TRATAMENTO);
        admission.setHealthRecord(null);
        admission.setDiagnostics(null);
        admission.setPostOperatives(null);

        HospitalAdmissionResponseDTO responseDTO = new HospitalAdmissionResponseDTO(admission);

        assertNotNull(responseDTO);
        assertEquals(admission.getId(), responseDTO.getId());
        assertEquals(admission.getDate(), responseDTO.getDate());
        assertEquals(admission.getReasonHospitalization(), responseDTO.getReasonHospitalization());
        assertEquals(admission.getDateMedicalDischarge(), responseDTO.getDateMedicalDischarge());
        assertEquals(admission.getDateReturns(), responseDTO.getDateReturns());
        assertNull(responseDTO.getMedicalEvolution());
        assertEquals(admission.getTreatmentNextSteps(), responseDTO.getTreatmentNextSteps());
        assertNull(responseDTO.getHealthRecordId());
        assertNotNull(responseDTO.getDiagnosticIds());
        assertTrue(responseDTO.getDiagnosticIds().isEmpty());
        assertNotNull(responseDTO.getPostOperativeIds());
        assertTrue(responseDTO.getPostOperativeIds().isEmpty());
    }

    private HospitalAdmissionCreateDTO getFakeHospitalAdmissionDTO() {
        HospitalAdmissionCreateDTO dto = new HospitalAdmissionCreateDTO();
        dto.setDate(LocalDate.of(2025, 4, 24));
        dto.setReasonHospitalization(ReasonHospitalization.HEMATOLOGIA);
        dto.setDateMedicalDischarge(LocalDate.of(2025, 4, 27));
        dto.setDateReturns(LocalDate.of(2025, 5, 10));
        dto.setMedicalEvolution(MedicalEvolution.PROGRESSO_FAVORAVEL);
        dto.setTreatmentNextSteps(TreatmentNextSteps.ALTA_MEDICA);
        dto.setHealthRecordId(1L);
        dto.setDiagnosticIds(Set.of(1L));
        dto.setPostOperativeIds(Set.of(1L));
        return dto;
    }

    private HospitalAdmission getFakeHospitalAdmission() {
        HospitalAdmission ha = new HospitalAdmission();
        ha.setReasonHospitalization(ReasonHospitalization.HEMATOLOGIA);
        ha.setDate(LocalDate.of(2025, 4, 24));
        ha.setDateMedicalDischarge(LocalDate.of(2025, 4, 27));
        ha.setDateReturns(LocalDate.of(2025, 5, 10));
        ha.setMedicalEvolution(MedicalEvolution.QUADRO_GRAVE);
        ha.setTreatmentNextSteps(TreatmentNextSteps.CONTINUOU_TRATAMENTO);
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
        return record;
    }

    public DogBreed getFakeDogBreed() {
        return new DogBreed(1L, "2", "Golden Retriever", "Amigável e inteligente e esperto",
                10, 12, 30D, 34D, 25D, 29D,
                false);
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
