package com.project.vetdata.service;



import com.project.vetdata.dto.HealthRecordCreateDTO;
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
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.Set;

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
        breed.setSize("Grande");
        breed.setHypoallergenic(false);
        return breed;
    }

    private HealthRecordCreateDTO getFakeHealthRecordDTO(Long breedId) {
        HealthRecordCreateDTO dto = new HealthRecordCreateDTO();
        dto.setAdmission(LocalDate.of(2025, 4, 8));
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
        dto.setAdmission(LocalDate.of(2025, 4, 8));
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
