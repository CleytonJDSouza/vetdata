package com.project.vetdata.service;



import com.project.vetdata.dto.HealthRecordCreateDTO;
import com.project.vetdata.dto.HealthRecordUpdateDTO;
import com.project.vetdata.enums.DogSize;
import com.project.vetdata.enums.Euthanasia;
import com.project.vetdata.enums.Gender;
import com.project.vetdata.model.DogBreed;
import com.project.vetdata.model.HealthRecord;
import com.project.vetdata.repository.DogBreedRepository;
import com.project.vetdata.repository.HealthRecordRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
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
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class HealthRecordServiceImplIT {

    @Container
    private static final MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8.0.26");

    @Autowired
    private HealthRecordServiceImpl healthRecordService;

    @Autowired
    private HealthRecordRepository healthRecordRepository;

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
    public void given_ValidHealthRecordCreateDTO_when_createHealthRecord_then_RecordIsCreated() {
        DogBreed breed = createFakeBreed();
        dogBreedRepository.save(breed);

        HealthRecordCreateDTO dto = getFakeHealthRecordDTO(breed.getId());
        HealthRecord saved = healthRecordService.createHealthRecord(dto);

        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertEquals(dto.getPatient(), saved.getPatient());
        assertEquals(dto.getBreedId(), saved.getBreed().getId());
    }

    @Test
    public void given_InvalidBreedId_when_createHealthRecord_then_ThrowsException() {
        Long invalidBreedId = 999L;

        HealthRecordCreateDTO dto = getFakeHealthRecordDTO(invalidBreedId);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            healthRecordService.createHealthRecord(dto);
        });

        assertEquals("Raça " + invalidBreedId + " não encontrada", exception.getMessage());
    }

    @Test
    public void given_InvalidHealthRecordCreateDTOWithoutPatient_when_ValidateDTO_then_ThrowsConstraintViolationException() {
        HealthRecordCreateDTO invalidDTO = getInvalidFakeHealthRecordDTO();

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<HealthRecordCreateDTO>> violations = validator.validate(invalidDTO);

        assertFalse(violations.isEmpty(), "Deve chamar ConstraintViolationException");

        boolean hasPatientViolation = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("patient"));

        assertTrue(hasPatientViolation, "Deve ter erro no campo 'patient'");
    }

    @Test
    public void given_valid_updateDTO_when_updateHealthRecord_then_record_is_updated() {
        DogBreed breed = dogBreedRepository.save(createFakeBreed());
        HealthRecordCreateDTO createDTO = getFakeHealthRecordDTO(breed.getId());
        HealthRecord original = healthRecordService.createHealthRecord(createDTO);

        HealthRecordUpdateDTO updateDTO = new HealthRecordUpdateDTO();
        updateDTO.setPatient("Nutella");
        updateDTO.setTutor("Aline");
        updateDTO.setWeight(27.3);
        updateDTO.setBreedId(breed.getId());
        updateDTO.setEuthanasia(Euthanasia.NO);
        updateDTO.setGender(Gender.FEMALE);
        updateDTO.setSize(DogSize.MEDIUM);

        HealthRecord updated = healthRecordService.updateHealthRecord(original.getId(), updateDTO);

        assertNotNull(updated);
        assertEquals("Nutella", updated.getPatient());
        assertEquals("Aline", updated.getTutor());
        assertEquals(27.3, updated.getWeight());
    }

    @Test
    public void given_invalid_id_when_updateHealthRecord_then_throwsException() {
        Long invalidId = 99L;

        HealthRecordUpdateDTO updateDTO = new HealthRecordUpdateDTO();
        updateDTO.setPatient("Nutella");
        updateDTO.setTutor("Aline");
        updateDTO.setWeight(27.3);
        updateDTO.setBreedId(1L);
        updateDTO.setEuthanasia(Euthanasia.NO);
        updateDTO.setGender(Gender.FEMALE);
        updateDTO.setSize(DogSize.MEDIUM);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            healthRecordService.updateHealthRecord(invalidId, updateDTO);
        });

        assertEquals("Prontuário não encontrado com ID: " + invalidId, exception.getMessage());
    }

    @Test
    public void given_InvalidBreedId_when_updateHealthRecord_then_ThrowsException() {
        DogBreed breed = dogBreedRepository.save(createFakeBreed());
        HealthRecordCreateDTO createDTO = getFakeHealthRecordDTO(breed.getId());
        HealthRecord saved = healthRecordService.createHealthRecord(createDTO);

        Long invalidBreedId = 999L;

        HealthRecordUpdateDTO dto = new HealthRecordUpdateDTO();
        dto.setBreedId(invalidBreedId);
        dto.setEuthanasia(Euthanasia.NO);
        dto.setGender(Gender.FEMALE);
        dto.setPatient("Ciri");
        dto.setSize(DogSize.MEDIUM);
        dto.setTutor("Cleyton");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            healthRecordService.updateHealthRecord(saved.getId(), dto);
        });

        assertEquals("Raça " + invalidBreedId + " não encontrada", exception.getMessage());
    }

    @Test
    public void given_existing_healthRecord_when_get_all_healthRecods_then_returns_page_results() {
        DogBreed breed = dogBreedRepository.save(createFakeBreed());

        healthRecordService.createHealthRecord(getFakeHealthRecordDTO(breed.getId()));
        healthRecordService.createHealthRecord(getFakeHealthRecordDTO2(breed.getId()));

        Pageable pageable = PageRequest.of(0,10);
        Page<HealthRecord> result = healthRecordService.findAll(pageable);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(2, result.getTotalElements());

        List<String> patients = result.getContent().stream()
                .map(HealthRecord::getPatient)
                .collect(Collectors.toList());

        assertTrue(patients.contains("Arya"));
        assertTrue(patients.contains("Amora"));
    }

    @Test
    public void give_no_healthRecord_in_database_when_get_all_healthRecord_then_returns_empty_page() {
        Pageable pageable = PageRequest.of(0, 5);
        Page<HealthRecord> result = healthRecordService.findAll(pageable);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        assertEquals(0, result.getTotalElements());
    }

    @Test
    public void given_valid_is_when_deleteHealthRecord_then_record_is_deleted() {
        DogBreed breed = dogBreedRepository.save(createFakeBreed());
        HealthRecordCreateDTO createDTO = getFakeHealthRecordDTO(breed.getId());
        HealthRecord created = healthRecordService.createHealthRecord(createDTO);

        assertNotNull(created.getId(), "Confere se o prontuário foi salvo");

        Long id = created.getId();
        healthRecordService.deleteHealthRecord(id);

        Optional<HealthRecord> deleted = healthRecordRepository.findById(id);
        assertFalse(deleted.isPresent(), "O prontuário deve ter sido deletado");
    }

    @Test
    public void given_invalid_id_when_deleteHealthRecord_then_no_exception_thrown() {
        Long invalidId = 999L;

        try {
            healthRecordService.deleteHealthRecord(invalidId);
        } catch (Exception ex) {
            fail("Não deve ter exceções");
        }
    }

    @Test
    public void given_existing_healthRecord_when_getHealthRecordById_then_returns_heathRecord() {
        DogBreed breed = dogBreedRepository.save(createFakeBreed());
        HealthRecordCreateDTO createDTO = getFakeHealthRecordDTO(breed.getId());
        HealthRecord created = healthRecordService.createHealthRecord(createDTO);

        Optional<HealthRecord> result = healthRecordService.getHealthRecordById(created.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(created.getId());
        assertThat(result.get().getPatient()).isEqualTo(created.getPatient());
    }

    @Test
    public void given_non_existing_healthRecord_when_getHealthRecordById_then_returns_empty_optional() {
        Long nonExistenId = 99L;

        Optional<HealthRecord> result = healthRecordService.getHealthRecordById(nonExistenId);

        assertThat(result).isNotPresent();
    }

    private DogBreed createFakeBreed() {
        DogBreed breed = new DogBreed();
        breed.setName("Golden Retriever");
        breed.setIdExternalApi("golden123");
        breed.setDescription("Amável e brincalhão");
        breed.setLifeExpectancyMin(10);
        breed.setLifeExpectancyMax(12);
        breed.setMaleWeightMin(30.0);
        breed.setMaleWeightMax(34.0);
        breed.setFemaleWeightMin(28.0);
        breed.setFemaleWeightMax(32.0);
        breed.setHypoallergenic(false);
        return breed;
    }

    private HealthRecordCreateDTO getFakeHealthRecordDTO(Long breedId) {
        HealthRecordCreateDTO dto = new HealthRecordCreateDTO();
        dto.setAge(5.0);
        dto.setCodPatient("C125");
        dto.setColor("Branco");
        dto.setDeath(false);
        dto.setEuthanasia(Euthanasia.NO);
        dto.setGender(Gender.FEMALE);
        dto.setPatient("Arya");
        dto.setSize(DogSize.SMALL);
        dto.setTutor("Beatriz");
        dto.setWeight(32.5);
        dto.setBreedId(breedId);
        return dto;
    }

    private HealthRecordCreateDTO getFakeHealthRecordDTO2(Long breedId) {
        HealthRecordCreateDTO dto = new HealthRecordCreateDTO();
        dto.setAge(5.0);
        dto.setCodPatient("C123");
        dto.setColor("Branco");
        dto.setDeath(false);
        dto.setEuthanasia(Euthanasia.NO);
        dto.setGender(Gender.FEMALE);
        dto.setPatient("Amora");
        dto.setSize(DogSize.SMALL);
        dto.setTutor("Cleyton");
        dto.setWeight(32.5);
        dto.setBreedId(breedId);
        return dto;
    }

    private HealthRecordCreateDTO getInvalidFakeHealthRecordDTO() {
        HealthRecordCreateDTO dto = new HealthRecordCreateDTO();
        dto.setPatient(null);
        dto.setAge(4.00);
        dto.setCodPatient("C123");
        dto.setBreedId(1L);
        dto.setTutor("Beatriz");
        dto.setColor("Branco");
        dto.setWeight(25.0);
        dto.setSize(DogSize.MEDIUM);
        dto.setGender(Gender.FEMALE);
        dto.setEuthanasia(Euthanasia.NO);
        dto.setDeath(false);
        return dto;
    }
}
