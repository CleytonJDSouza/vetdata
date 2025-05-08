package com.project.vetdata.service;

import com.project.vetdata.dto.HealthRecordCreateDTO;
import com.project.vetdata.dto.HospitalAdmissionCreateDTO;
import com.project.vetdata.dto.HospitalAdmissionResponseDTO;
import com.project.vetdata.dto.HospitalAdmissionUpdateDTO;
import com.project.vetdata.enums.DogSize;
import com.project.vetdata.enums.Euthanasia;
import com.project.vetdata.enums.Gender;
import com.project.vetdata.model.*;
import com.project.vetdata.repository.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class HospitalAdmissionServiceImplIT {

    @Container
    private static final MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8.0.26");

    @Autowired
    private HospitalAdmissionServiceImpl hospitalAdmissionService;

    @Autowired
    private HealthRecordRepository healthRecordRepository;

    @Autowired
    private HospitalAdmissionRepository hospitalAdmissionRepository;

    @Autowired
    private PostOperativeRepository postOperativeRepository;

    @Autowired
    private DiagnosticRepository diagnosticRepository;

    @Autowired
    private DogBreedRepository dogBreedRepository;

    @BeforeAll
    static void beforeAll() {
        mysqlContainer.start();
    }

    @AfterAll
    static void afterAll() {
        mysqlContainer.stop();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mysqlContainer::getUsername);
        registry.add("spring.datasource.password", mysqlContainer::getPassword);
    }

    @Test
    void connectionEstablished() {
        assertThat(mysqlContainer.isCreated()).isTrue();
        assertThat(mysqlContainer.isRunning()).isTrue();
    }

    @Test
    public void given_validDTO_when_createHospitalAdmission_then_admission_is_created() {
        DogBreed breed = dogBreedRepository.save(createFakeBreed());
        HealthRecord healthRecord = healthRecordRepository.save(createFakeHealthRecord(breed));
        PostOperative postOperative = postOperativeRepository.save(createFakePostOperative());
        Diagnostic diagnostic = diagnosticRepository.save(createFakeDiagnostic());

        HospitalAdmissionCreateDTO dto = getFakeHospitalAdmissionDTO(healthRecord.getId(), List.of(postOperative.getId()), List.of(diagnostic.getId()));

        HospitalAdmissionResponseDTO admission = hospitalAdmissionService.createHospitalAdmission(dto);

        assertNotNull(admission.getId());
        assertEquals(dto.getReasonHospitalization(), admission.getReasonHospitalization());
        assertEquals(dto.getHealthRecordId(), admission.getHealthRecordId());
        assertNotNull(admission.getPostOperativeIds());
        assertEquals(1, admission.getPostOperativeIds().size());
        assertNotNull(admission.getDiagnosticIds());
        assertEquals(1, admission.getDiagnosticIds().size());
    }


    @Test
    public void given_invalid_healthRecordId_when_createHospitalAdmission_then_throws_exception() {
        HospitalAdmissionCreateDTO dto = getFakeHospitalAdmissionDTO(99L, List.of(), List.of());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            hospitalAdmissionService.createHospitalAdmission(dto);
        });

        assertEquals("Prontuário com ID 99 não encontrado", exception.getMessage());
    }

    @Test
    public void given_invalid_postOperativeId_when_createHospitalAdmission_then_throws_exception() {
        DogBreed breed = dogBreedRepository.save(createFakeBreed());
        HealthRecord healthRecord = healthRecordRepository.save(createFakeHealthRecord(breed));
        Diagnostic diagnostic = diagnosticRepository.save(createFakeDiagnostic());

        HospitalAdmissionCreateDTO dto = getFakeHospitalAdmissionDTO(
                healthRecord.getId(),
                List.of(99L),
                List.of(diagnostic.getId())
        );

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            hospitalAdmissionService.createHospitalAdmission(dto);
        });

        assertEquals("Pós Operatório não encontrado", exception.getMessage());
    }

    @Test
    public void given_invalid_diagnosticId_when_createHospitalAdmission_then_throws_exception() {
        DogBreed breed = dogBreedRepository.save(createFakeBreed());
        HealthRecord healthRecord = healthRecordRepository.save(createFakeHealthRecord(breed));
        PostOperative postOperative = postOperativeRepository.save(createFakePostOperative());

        HospitalAdmissionCreateDTO dto = getFakeHospitalAdmissionDTO(
                healthRecord.getId(),
                List.of(postOperative.getId()),
                List.of(99L)
        );

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            hospitalAdmissionService.createHospitalAdmission(dto);
        });

        assertEquals("Diagnóstico não encontrado", exception.getMessage());
    }

    @Test
    public void given_valid_updateDTO_when_updateHospitalAdmission_then_admission_is_updated() {
        DogBreed breed = dogBreedRepository.save(createFakeBreed());
        HealthRecord healthRecord = healthRecordRepository.save(createFakeHealthRecord(breed));
        PostOperative postOperative = postOperativeRepository.save(createFakePostOperative());
        Diagnostic diagnostic = diagnosticRepository.save((createFakeDiagnostic()));

        HospitalAdmissionCreateDTO createDTO = getFakeHospitalAdmissionDTO(
                healthRecord.getId(),
                List.of(postOperative.getId()),
                List.of(diagnostic.getId()));

        HospitalAdmissionResponseDTO createdAdmission = hospitalAdmissionService.createHospitalAdmission(createDTO);

        PostOperative newPostOperative = postOperativeRepository.save(createFakePostOperative());
        newPostOperative.setDescription("Reabilitação");
        Diagnostic newDiagnostic = diagnosticRepository.save(createFakeDiagnostic());
        newDiagnostic.setDescription("Alergia");

        HospitalAdmissionUpdateDTO updateDTO = new HospitalAdmissionUpdateDTO();
        updateDTO.setDate(LocalDate.of(2025, 7, 1));
        updateDTO.setReasonHospitalization("Cancer");
        updateDTO.setDateMedicalDischarge(LocalDate.of(2025, 9, 1));
        updateDTO.setDateReturns(LocalDate.of(2025, 1, 1));
        updateDTO.setMedicalEvolution("Evoluindo bem");
        updateDTO.setTreatmentNextSteps("Continuar tratamento");
        updateDTO.setDiagnosticIds(Set.of(newDiagnostic.getId()));
        updateDTO.setPostOperativeIds(Set.of(newPostOperative.getId()));

        HospitalAdmissionResponseDTO updatedAdmission = hospitalAdmissionService.updateHospitalAdmission(createdAdmission.getId(), updateDTO);

        assertNotNull(updatedAdmission);
        assertEquals("Cancer", updatedAdmission.getReasonHospitalization());
        assertEquals(LocalDate.of(2025, 7, 1), updatedAdmission.getDate());
        assertEquals(LocalDate.of(2025, 9, 1), updatedAdmission.getDateMedicalDischarge());
        assertEquals(LocalDate.of(2025, 1, 1), updatedAdmission.getDateReturns());
        assertEquals("Evoluindo bem", updatedAdmission.getMedicalEvolution());
        assertEquals("Continuar tratamento", updatedAdmission.getTreatmentNextSteps());
        assertEquals(1, updatedAdmission.getDiagnosticIds().size());
        assertTrue(updatedAdmission.getDiagnosticIds().contains(newDiagnostic.getId()));
        assertEquals(1, updatedAdmission.getPostOperativeIds().size());
        assertTrue(updatedAdmission.getPostOperativeIds().contains(newPostOperative.getId()));
    }

    @Test
    public void given_invalid_id_when_updateHospitalAdmission_then_throws_exception() {
        Long invalidId = 99L;

        HospitalAdmissionUpdateDTO updateDTO = new HospitalAdmissionUpdateDTO();
        updateDTO.setDate(LocalDate.of(2025, 7, 1));
        updateDTO.setReasonHospitalization("Cancer");
        updateDTO.setDateMedicalDischarge(LocalDate.of(2025, 9, 1));
        updateDTO.setDateReturns(LocalDate.of(2025, 1, 1));
        updateDTO.setMedicalEvolution("Evoluindo bem");
        updateDTO.setTreatmentNextSteps("Continuar tratamento");
        updateDTO.setDiagnosticIds(Set.of());
        updateDTO.setPostOperativeIds(Set.of());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            hospitalAdmissionService.updateHospitalAdmission(invalidId, updateDTO);
        });

        assertEquals("Internação com ID " + invalidId + " não encontrada", exception.getMessage());
    }

    @Test
    public void given_existing_hospitalAdmissions_when_get_all_hospitalAdmission_then_returns_page_results() {
        DogBreed breed = dogBreedRepository.save(createFakeBreed());
        HealthRecord healthRecord = healthRecordRepository.save(createFakeHealthRecord(breed));
        PostOperative postOperative = postOperativeRepository.save(createFakePostOperative());
        Diagnostic diagnostic = diagnosticRepository.save(createFakeDiagnostic());

        HospitalAdmissionCreateDTO dto1 = getFakeHospitalAdmissionDTO(
                healthRecord.getId(),
                List.of(postOperative.getId()),
                List.of(diagnostic.getId())
        );

        HospitalAdmissionCreateDTO dto2 = getFakeHospitalAdmissionDTO2(
                healthRecord.getId(),
                List.of(postOperative.getId()),
                List.of(diagnostic.getId())
        );

        hospitalAdmissionService.createHospitalAdmission(dto1);
        hospitalAdmissionService.createHospitalAdmission(dto2);

        List<HospitalAdmissionResponseDTO> result = hospitalAdmissionService.findAll();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(2, result.size());

        List<String> reasons = result.stream()
                .map(HospitalAdmissionResponseDTO::getReasonHospitalization)
                .collect(Collectors.toList());

        assertTrue(reasons.contains("Infecção grave"));
        assertTrue(reasons.contains("Teste"));
    }

    @Test
    public void given_no_hospitalAdmission_in_database_when_get_all_hospitalAdmission_then_returns_empty_page() {
        List<HospitalAdmissionResponseDTO> result = hospitalAdmissionService.findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        assertEquals(0, result.size());
    }

    private DogBreed createFakeBreed() {
        DogBreed breed = new DogBreed();
        breed.setName("Pug");
        breed.setIdExternalApi("pug123");
        breed.setDescription("Pequeno e simpático");
        breed.setLifeExpectancyMin(10);
        breed.setLifeExpectancyMax(14);
        breed.setMaleWeightMin(6.0);
        breed.setMaleWeightMax(8.0);
        breed.setFemaleWeightMin(6.0);
        breed.setFemaleWeightMax(8.0);
        breed.setSize("Pequeno");
        breed.setHypoallergenic(false);
        return breed;
    }

    private PostOperative createFakePostOperative() {
        PostOperative postOperative = new PostOperative();
        postOperative.setDescription("Fisioterapia");
        return postOperative;
    }

    private Diagnostic createFakeDiagnostic() {
        Diagnostic diagnostic = new Diagnostic();
        diagnostic.setDescription("Cancer");
        return diagnostic;
    }

    private HealthRecord createFakeHealthRecord(DogBreed breed) {
        HealthRecord record = new HealthRecord();
        record.setAge(5.0);
        record.setCodPatient("C125");
        record.setColor("Branco");
        record.setDeath(false);
        record.setEuthanasia(Euthanasia.NO);
        record.setGender(Gender.FEMALE);
        record.setPatient("Arya");
        record.setSize(DogSize.SMALL);
        record.setTutor("Beatriz");
        record.setWeight(32.5);
        record.setBreed(breed);
        return record;
    }

    private HospitalAdmissionCreateDTO getFakeHospitalAdmissionDTO(Long healthRecordId, List<Long> postOperativeIds, List<Long> diagnosticIds) {
        HospitalAdmissionCreateDTO dto = new HospitalAdmissionCreateDTO();
        dto.setDate(LocalDate.of(2025, 4, 10));
        dto.setReasonHospitalization("Infecção grave");
        dto.setDateMedicalDischarge(LocalDate.of(2025, 4, 20));
        dto.setDateReturns(LocalDate.of(2025, 5, 1));
        dto.setMedicalEvolution("Teste");
        dto.setTreatmentNextSteps("Repouso e medicação");
        dto.setHealthRecordId(healthRecordId);
        dto.setPostOperativeIds(new HashSet<>(postOperativeIds));
        dto.setDiagnosticIds(new HashSet<>(diagnosticIds));
        return dto;
    }

    private HospitalAdmissionCreateDTO getFakeHospitalAdmissionDTO2 (Long healthRecordId, List<Long> postOperativeIds, List<Long> diagnosticIds) {
        HospitalAdmissionCreateDTO dto = new HospitalAdmissionCreateDTO();
        dto.setDate(LocalDate.of(2025, 4, 10));
        dto.setReasonHospitalization("Teste");
        dto.setDateMedicalDischarge(LocalDate.of(2025, 4, 20));
        dto.setDateReturns(LocalDate.of(2025, 5, 1));
        dto.setMedicalEvolution("Teste");
        dto.setTreatmentNextSteps("Teste");
        dto.setHealthRecordId(healthRecordId);
        dto.setPostOperativeIds(new HashSet<>(postOperativeIds));
        dto.setDiagnosticIds(new HashSet<>(diagnosticIds));
        return dto;
    }
}
