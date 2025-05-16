package com.project.vetdata.service;

import com.project.vetdata.dto.DogBreedCreateDTO;
import com.project.vetdata.dto.DogBreedUpdateDTO;
import com.project.vetdata.exception.BreedNotFoundException;
import com.project.vetdata.model.DogBreed;
import com.project.vetdata.repository.DogBreedRepository;
import jakarta.validation.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class DogBreedServiceImplIT {

    @Container
    private static final MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8.0.26");

    @Autowired
    private DogBreedRepository dogBreedRepository;

    @Autowired
    private DogBreedServiceImpl dogBreedService;

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
    public void given_ExistingDogBreeds_when_getAllDogBreeds_then_ReturnsPagedResults() {
        DogBreed dogBreed = getFakeDogBreed();
        dogBreedRepository.save(dogBreed);

        Pageable pageable = PageRequest.of(0, 5);
        Page<DogBreed> result = dogBreedService.getAllDogBreeds(pageable);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.getTotalElements());
        assertEquals("Labrador", result.getContent().get(0).getName());
    }

    @Test
    public void given_NonDogBreedsInDatabase_when_getAllDogBreeds_then_ReturnsEmptyPage() {
        Pageable pageable = PageRequest.of(0, 5);
        Page<DogBreed> result = dogBreedService.getAllDogBreeds(pageable);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        assertEquals(0, result.getTotalElements());
    }

    @Test
    public void given_ExistingDogBreedId_when_getDogBreedId_then_ReturnsDogBreed() {
        DogBreed dogBreed = getFakeDogBreed();
        dogBreed = dogBreedRepository.save(dogBreed);

        Optional<DogBreed> result = dogBreedService.getDogBreedId(dogBreed.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(dogBreed.getId());
        assertThat(result.get().getName()).isEqualTo(dogBreed.getName());
    }

    @Test
    public void given_NonExistingDogBreedId_when_getDogBreedId_then_ReturnsEmptyOptional() {
        Long nonExistentId = 999L;

        Optional<DogBreed> result = dogBreedService.getDogBreedId(nonExistentId);

        assertThat(result).isNotPresent();
    }

    @Test
    public void given_ValidDogBreedCreateDTO_when_createDogBreed_then_ReturnsCreatedDogBreed() {
        DogBreedCreateDTO dogBreedCreateDTO = getFakeDogBreedCreateDTO();
        DogBreed createdDogBreed = dogBreedService.createDogBreed(dogBreedCreateDTO);

        assertNotNull(createdDogBreed);
        assertNotNull(createdDogBreed.getId());
        assertEquals(dogBreedCreateDTO.getName(), createdDogBreed.getName());
    }

    @Test
    public void given_InvalidDogBreedCreateDTOWithoutName_when_CreateDogBreed_then_ThrowsConstraintViolationException() {
        DogBreedCreateDTO invalidDogBreed = getInvalidFakeDogBreedCreateDTO();

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<DogBreedCreateDTO>> violations = validator.validate(invalidDogBreed);

        assertFalse(violations.isEmpty(), "Deve chamar ConstraintViolationException");
    }

    @Test
    public void given_ExistingDogBreedId_when_deleteDogBreed_then_DogBreedIsDeleted() {
        DogBreed dogBreed = getFakeDogBreed();
        dogBreed = dogBreedRepository.save(dogBreed);

        dogBreedService.deleteDogBreed(dogBreed.getId());

        Optional<DogBreed> deletedDogBreed = dogBreedRepository.findById(dogBreed.getId());
        assertThat(deletedDogBreed).isNotPresent();
    }

    @Test
    public void given_NonExistingDogBreedId_when_deleteDogBreed_then_NoExceptionIsThrown() {
        Long nonExistentId = 999L;

        try {
            dogBreedService.deleteDogBreed(nonExistentId);
        } catch (Exception ex) {
            fail("Raça não encontrada!");
        }
    }

    @Test
    public void given_ExistingDogBreedId_when_updateDogBreed_then_ReturnsUpdatedDogBreed() {
        DogBreed dogBreed = getFakeDogBreed();
        dogBreed = dogBreedRepository.save(dogBreed);

        DogBreedUpdateDTO dogBreedUpdateDTO = new DogBreedUpdateDTO();
        dogBreedUpdateDTO.setDescription("Legal");
        dogBreedUpdateDTO.setLifeExpectancyMin(13);
        dogBreedUpdateDTO.setLifeExpectancyMax(15);
        dogBreedUpdateDTO.setMaleWeightMin(31D);
        dogBreedUpdateDTO.setMaleWeightMax(37D);
        dogBreedUpdateDTO.setFemaleWeightMin(28D);
        dogBreedUpdateDTO.setFemaleWeightMax(34D);
        dogBreedUpdateDTO.setHypoallergenic(true);

        DogBreed updatedDogBreed = dogBreedService.updateDogBreed(dogBreed.getId(),dogBreedUpdateDTO);

        assertEquals("Legal", updatedDogBreed.getDescription());
        assertEquals(13, updatedDogBreed.getLifeExpectancyMin());
        assertEquals(15, updatedDogBreed.getLifeExpectancyMax());
        assertEquals(31D, updatedDogBreed.getMaleWeightMin());
        assertEquals(37D, updatedDogBreed.getMaleWeightMax());
        assertEquals(28D, updatedDogBreed.getFemaleWeightMin());
        assertEquals(34D, updatedDogBreed.getFemaleWeightMax());
        assertEquals(true, updatedDogBreed.getHypoallergenic());

    }

    @Test
    public void given_NonExistingDogBreeId_when_updateDogBreed_then_ThrowsBreedNotFoundException() {
        Long nonExistentId = 999L;

        DogBreedUpdateDTO dogBreedUpdateDTO = new DogBreedUpdateDTO();
        dogBreedUpdateDTO.setDescription("Legal");

        try {
            dogBreedService.updateDogBreed(nonExistentId, dogBreedUpdateDTO);
            fail("BreedNotFoundException");
        } catch (BreedNotFoundException ex) {
            assertEquals("Raça não encontrada!" + nonExistentId, ex.getMessage());
        }
    }

    @Test
    public void given_ExistingDogBreedId_when_updateDogBreedWithPartialData_then_OnlyUpdatedFieldsAreChanged() {
        DogBreed dogBreed = getFakeDogBreed();
        dogBreed = dogBreedRepository.save(dogBreed);

        DogBreedUpdateDTO dogBreedUpdateDTO = new DogBreedUpdateDTO();
        dogBreedUpdateDTO.setFemaleWeightMax(34D);

        DogBreed updatedDogBreed = dogBreedService.updateDogBreed(dogBreed.getId(), dogBreedUpdateDTO);

        assertEquals(34D, updatedDogBreed.getFemaleWeightMax());
        assertEquals(dogBreed.getFemaleWeightMin(), updatedDogBreed.getFemaleWeightMin());
    }

    @Test
    public void given_InitialsOfDogBreedName_when_getBySearchTerm_then_ReturnsMatchingDogBreeds() {
        DogBreed labrador = getFakeDogBreed();
        dogBreedRepository.save(labrador);

        DogBreed lhasa = getFakeDogBreed2();
        dogBreedRepository.save(lhasa);

        DogBreed poodle = getFakeDogBreed3();
        dogBreedRepository.save(poodle);

        Pageable pageable = PageRequest.of(0,5);
        Page<DogBreed> result = dogBreedService.getBySearchTerm("L", pageable);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(2, result.getTotalElements());
        assertThat(result.getContent()).extracting(DogBreed::getName).containsExactlyInAnyOrder("Labrador", "Lhasa Apso");
    }

    @Test
    public void given_term_in_different_case_when_getBySearchTerm_then_returns_matching_DogBreeds() {
        DogBreed labrador = getFakeDogBreed();
        dogBreedRepository.save(labrador);

        DogBreed lhasa = getFakeDogBreed2();
        dogBreedRepository.save(lhasa);

        DogBreed poodle = getFakeDogBreed3();
        dogBreedRepository.save(poodle);

        Pageable pageable = PageRequest.of(0, 5);
        Page<DogBreed> result = dogBreedService.getBySearchTerm("l", pageable);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(2, result.getTotalElements());
        assertThat(result.getContent()).extracting(DogBreed::getName).containsExactly("Labrador", "Lhasa Apso");
    }


    private DogBreed getFakeDogBreed() {
        DogBreed dogBreed = new DogBreed();
        dogBreed.setIdExternalApi("1");
        dogBreed.setName("Labrador");
        dogBreed.setDescription("Leal e carinhoso");
        dogBreed.setLifeExpectancyMin(12);
        dogBreed.setLifeExpectancyMax(14);
        dogBreed.setMaleWeightMin(30D);
        dogBreed.setMaleWeightMax(36D);
        dogBreed.setFemaleWeightMin(27D);
        dogBreed.setFemaleWeightMax(33D);
        dogBreed.setHypoallergenic(false);
        return dogBreed;
    }

    private DogBreed getFakeDogBreed2() {
        DogBreed dogBreed = new DogBreed();
        dogBreed.setIdExternalApi("2");
        dogBreed.setName("Lhasa Apso");
        dogBreed.setDescription("Leal e carinhoso");
        dogBreed.setLifeExpectancyMin(12);
        dogBreed.setLifeExpectancyMax(14);
        dogBreed.setMaleWeightMin(30D);
        dogBreed.setMaleWeightMax(36D);
        dogBreed.setFemaleWeightMin(27D);
        dogBreed.setFemaleWeightMax(33D);
        dogBreed.setHypoallergenic(false);
        return dogBreed;
    }

    private DogBreed getFakeDogBreed3() {
        DogBreed dogBreed = new DogBreed();
        dogBreed.setIdExternalApi("3");
        dogBreed.setName("Poodle");
        dogBreed.setDescription("Leal e carinhoso");
        dogBreed.setLifeExpectancyMin(12);
        dogBreed.setLifeExpectancyMax(14);
        dogBreed.setMaleWeightMin(30D);
        dogBreed.setMaleWeightMax(36D);
        dogBreed.setFemaleWeightMin(27D);
        dogBreed.setFemaleWeightMax(33D);
        dogBreed.setHypoallergenic(false);
        return dogBreed;
    }

    private DogBreedCreateDTO getFakeDogBreedCreateDTO() {
        DogBreedCreateDTO dogBreedCreateDTO = new DogBreedCreateDTO();
        dogBreedCreateDTO.setName("Labrador");
        dogBreedCreateDTO.setDescription("Leal e carinhoso");
        dogBreedCreateDTO.setLifeExpectancyMin(12);
        dogBreedCreateDTO.setLifeExpectancyMax(14);
        dogBreedCreateDTO.setMaleWeightMin(30D);
        dogBreedCreateDTO.setMaleWeightMax(36D);
        dogBreedCreateDTO.setFemaleWeightMin(27D);
        dogBreedCreateDTO.setFemaleWeightMax(33D);
        dogBreedCreateDTO.setHypoallergenic(false);
        return dogBreedCreateDTO;
    }

    private DogBreedCreateDTO getInvalidFakeDogBreedCreateDTO() {
        DogBreedCreateDTO dogBreedCreateDTO = new DogBreedCreateDTO();
        dogBreedCreateDTO.setName(null);
        dogBreedCreateDTO.setDescription("Leal e Carinhoso");
        dogBreedCreateDTO.setLifeExpectancyMin(12);
        dogBreedCreateDTO.setLifeExpectancyMax(14);
        dogBreedCreateDTO.setMaleWeightMin(30D);
        dogBreedCreateDTO.setMaleWeightMax(36D);
        dogBreedCreateDTO.setFemaleWeightMin(27D);
        dogBreedCreateDTO.setFemaleWeightMax(33D);
        dogBreedCreateDTO.setHypoallergenic(false);
        return dogBreedCreateDTO;
    }
}