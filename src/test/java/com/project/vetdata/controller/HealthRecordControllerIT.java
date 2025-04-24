package com.project.vetdata.controller;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.project.vetdata.dto.HealthRecordCreateDTO;
import com.project.vetdata.dto.HealthRecordUpdateDTO;
import com.project.vetdata.enums.DogSize;
import com.project.vetdata.enums.Euthanasia;
import com.project.vetdata.enums.Gender;
import com.project.vetdata.model.DogBreed;
import com.project.vetdata.model.HealthRecord;
import com.project.vetdata.repository.DogBreedRepository;
import com.project.vetdata.repository.HealthRecordRepository;
import io.restassured.RestAssured;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class HealthRecordControllerIT {

    @LocalServerPort
    private Integer port;

    @Autowired
    private HealthRecordRepository healthRecordRepository;

    @Autowired
    private DogBreedRepository dogBreedRepository;

    private static final int WIREMOCK_PORT = 8082;

    private static WireMockServer wireMockServer;

    @Container
    private static final MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8.0.26");

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        healthRecordRepository.deleteAll();
    }

    @BeforeAll
    static void beforeAll() {
        mysqlContainer.start();
        wireMockServer = new WireMockServer(WireMockConfiguration.options().port(WIREMOCK_PORT));
        wireMockServer.start();
        WireMock.configureFor("localhost", WIREMOCK_PORT);
    }

    @AfterAll
    static void afterAll() {
        mysqlContainer.stop();
        wireMockServer.stop();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mysqlContainer::getUsername);
        registry.add("spring.datasource.password", mysqlContainer::getPassword);
    }

    @Test
    public void given_valid_healthRecordCreateDTO_when_create_healthRecord_then_returns_created() {
        DogBreed dogBreed = new DogBreed(null, "2", "Pug", "Amigável e inteligente e esperto",
                10, 12, 30D, 34D, 25D, 29D,
                false, "Medio");
        DogBreed savedBreed = dogBreedRepository.save(dogBreed);

        HealthRecordCreateDTO dto = new HealthRecordCreateDTO();
        dto.setCodPatient("P01");
        dto.setTutor("Cleyton");
        dto.setPatient("Torresmo");
        dto.setBreedId(savedBreed.getId());
        dto.setAge(5.0);
        dto.setWeight(22.0);
        dto.setColor("Bege");
        dto.setSize(DogSize.MEDIUM);
        dto.setGender(Gender.MALE);
        dto.setDeath(false);
        dto.setEuthanasia(Euthanasia.NO);
        dto.setAdmission(LocalDate.of(2025, 1, 1));

        Long createdId =
                given()
                        .contentType("application/json")
                        .body(dto)
                        .when()
                        .post("/health-records")
                        .then()
                        .statusCode(201)
                        .body("patient", equalTo(dto.getPatient()))
                        .body("tutor", equalTo(dto.getTutor()))
                        .body("codPatient", equalTo(dto.getCodPatient()))
                        .extract()
                        .jsonPath()
                        .getLong("id");

        Optional<HealthRecord> optional = healthRecordRepository.findById(createdId);
        assertTrue(optional.isPresent(), "Prontuário não foi salvo no banco");

        HealthRecord saved = optional.get();
        assertEquals(dto.getPatient(), saved.getPatient());
        assertEquals(dto.getTutor(), saved.getTutor());
        assertEquals(dto.getGender(), saved.getGender());
        assertEquals(dto.getCodPatient(), saved.getCodPatient());
    }

    @Test
    public void given_invalid_healthRecordCreateDTO_when_create_healthRecord_then_returns_badRequest() {
        HealthRecordCreateDTO dto = new HealthRecordCreateDTO();
        dto.setCodPatient("");
        dto.setTutor("");
        dto.setPatient("");
        dto.setBreedId(null);
        dto.setAge(-5.0);
        dto.setWeight(-22.0);
        dto.setColor("");
        dto.setSize(DogSize.MEDIUM);
        dto.setGender(Gender.MALE);
        dto.setDeath(null);
        dto.setEuthanasia(Euthanasia.NO);
        dto.setAdmission(null);

        given()
                .contentType("application/json")
                .body(dto)
                .when()
                .post("/health-records")
                .then()
                .statusCode(400);

        List<HealthRecord> records = healthRecordRepository.findAll();
        assertTrue(records.isEmpty(), "Nenhum prontuário deve ser salvo no banco");
    }

    @Test
    public void given_valid_healthRecordId_and_valid_data_when_updateHealthRecord_then_returns_updatedHealthRecord() {
        DogBreed dogBreed = new DogBreed(null, "2", "Pug", "Amigável e inteligente e esperto",
                10, 12, 30D, 34D, 25D, 29D, false, "Medio");
        DogBreed savedBreed = dogBreedRepository.save(dogBreed);

        HealthRecord record = new HealthRecord();
        record.setCodPatient("P123");
        record.setTutor("Aline");
        record.setPatient("Nutella");
        record.setBreed(savedBreed);
        record.setAge(5.0);
        record.setWeight(28.0);
        record.setColor("Marrom");
        record.setSize(DogSize.LARGE);
        record.setGender(Gender.MALE);
        record.setDeath(false);
        record.setEuthanasia(Euthanasia.NO);
        record.setAdmission(LocalDate.of(2025, 4, 11));
        HealthRecord savedRecord = healthRecordRepository.save(record);

        savedRecord.getBreed().getName();

        HealthRecordUpdateDTO updateDTO = new HealthRecordUpdateDTO();
        updateDTO.setCodPatient("P124");
        updateDTO.setTutor("Andrea");
        updateDTO.setPatient("Cacau");
        updateDTO.setBreedId(savedBreed.getId());
        updateDTO.setAge(6.0);
        updateDTO.setWeight(29.0);
        updateDTO.setColor("Bege");
        updateDTO.setSize(DogSize.LARGE);
        updateDTO.setGender(Gender.MALE);
        updateDTO.setDeath(false);
        updateDTO.setEuthanasia(Euthanasia.NO);
        updateDTO.setAdmission(LocalDate.of(2025, 2, 1));

        given()
                .pathParam("id", savedRecord.getId())
                .contentType("application/json")
                .body(updateDTO)
                .when()
                .put("/health-records/{id}")
                .then()
                .statusCode(200)
                .body("codPatient", equalTo("P124"))
                .body("tutor", equalTo("Andrea"))
                .body("patient", equalTo("Cacau"))
                .body("color", equalTo("Bege"))
                .body("age", equalTo(6.0F))
                .body("weight", equalTo(29.0F));

        Optional<HealthRecord> optional = healthRecordRepository.findById(savedRecord.getId());
        assertTrue(optional.isPresent(), "O prontuário atualizado não foi encontrado.");

        HealthRecord updated = optional.get();
        assertEquals("P124", updated.getCodPatient());
        assertEquals("Andrea", updated.getTutor());
        assertEquals("Cacau", updated.getPatient());
        assertEquals("Bege", updated.getColor());
        assertEquals(6.0, updated.getAge());
        assertEquals(29.0, updated.getWeight());
        assertEquals(LocalDate.of(2025, 2, 1), updated.getAdmission());
    }

    @Test
    public void given_invalid_id_when_updateHealthRecord_then_returns_badRequest() {
        DogBreed dogBreed = new DogBreed(null, "2", "Pug", "Amigável e inteligente e esperto",
                10, 12, 30D, 34D, 25D, 29D, false, "Medio");
        DogBreed savedBreed = dogBreedRepository.save(dogBreed);

        HealthRecord record = new HealthRecord();
        record.setCodPatient("P123");
        record.setTutor("Aline");
        record.setPatient("Nutella");
        record.setBreed(savedBreed);
        record.setAge(5.0);
        record.setWeight(28.0);
        record.setColor("Marrom");
        record.setSize(DogSize.LARGE);
        record.setGender(Gender.MALE);
        record.setDeath(false);
        record.setEuthanasia(Euthanasia.NO);
        record.setAdmission(LocalDate.of(2025, 4, 11));
        HealthRecord savedRecord = healthRecordRepository.save(record);


        HealthRecordUpdateDTO updateDTO = new HealthRecordUpdateDTO();
        updateDTO.setCodPatient("P124");
        updateDTO.setTutor("Andrea");
        updateDTO.setPatient("Cacau");
        updateDTO.setBreedId(savedBreed.getId());
        updateDTO.setAge(6.0);
        updateDTO.setWeight(29.0);
        updateDTO.setColor("Bege");
        updateDTO.setSize(DogSize.LARGE);
        updateDTO.setGender(Gender.MALE);
        updateDTO.setDeath(false);
        updateDTO.setEuthanasia(Euthanasia.NO);
        updateDTO.setAdmission(LocalDate.of(2025, 2, 1));

        given()
                .pathParam("id", "abc")
                .contentType("application/json")
                .body(updateDTO)
                .when()
                .put("/health-records/{id}")
                .then()
                .statusCode(400);

        Optional<HealthRecord> optional = healthRecordRepository.findById(savedRecord.getId());
        assertTrue(optional.isPresent(), "O prontuário original deve se manter.");

        HealthRecord original = optional.get();
        assertEquals("P123", original.getCodPatient());
        assertEquals("Aline", original.getTutor());
        assertEquals("Nutella", original.getPatient());
        assertEquals("Marrom", original.getColor());
        assertEquals(5.0, original.getAge());
        assertEquals(28.0, original.getWeight());
        assertEquals(LocalDate.of(2025, 4, 11), original.getAdmission());
    }

    @Test
    public void given_non_existent_healthRecordId_when_updateHealthRecord_then_returns_notFound() {
        DogBreed dogBreed = new DogBreed(null, "2", "Pug", "Amigável e inteligente e esperto",
                10, 12, 30D, 34D, 25D, 29D, false, "Medio");
        DogBreed savedBreed = dogBreedRepository.save(dogBreed);

        HealthRecord record = new HealthRecord();
        record.setCodPatient("P123");
        record.setTutor("Aline");
        record.setPatient("Nutella");
        record.setBreed(savedBreed);
        record.setAge(5.0);
        record.setWeight(28.0);
        record.setColor("Marrom");
        record.setSize(DogSize.LARGE);
        record.setGender(Gender.MALE);
        record.setDeath(false);
        record.setEuthanasia(Euthanasia.NO);
        record.setAdmission(LocalDate.of(2025, 4, 11));
        HealthRecord savedRecord = healthRecordRepository.save(record);


        HealthRecordUpdateDTO updateDTO = new HealthRecordUpdateDTO();
        updateDTO.setCodPatient("P124");
        updateDTO.setTutor("Andrea");
        updateDTO.setPatient("Cacau");
        updateDTO.setBreedId(savedBreed.getId());
        updateDTO.setAge(6.0);
        updateDTO.setWeight(29.0);
        updateDTO.setColor("Bege");
        updateDTO.setSize(DogSize.LARGE);
        updateDTO.setGender(Gender.MALE);
        updateDTO.setDeath(false);
        updateDTO.setEuthanasia(Euthanasia.NO);
        updateDTO.setAdmission(LocalDate.of(2025, 2, 1));

        given()
                .pathParam("id", 999L)
                .contentType("application/json")
                .body(updateDTO)
                .when()
                .put("/health-records/{id}")
                .then()
                .statusCode(404);

        Optional<HealthRecord> optional = healthRecordRepository.findById(savedRecord.getId());
        assertTrue(optional.isPresent(), "O prontuário original deve se manter.");

        HealthRecord original = optional.get();
        assertEquals("P123", original.getCodPatient());
        assertEquals("Aline", original.getTutor());
        assertEquals("Nutella", original.getPatient());
        assertEquals("Marrom", original.getColor());
        assertEquals(5.0, original.getAge());
        assertEquals(28.0, original.getWeight());
        assertEquals(LocalDate.of(2025, 4, 11), original.getAdmission());
    }

    @Test
    public void given_healthRecordsInDatabase_when_getAllHealthRecords_then_returnsPagedList() {
        DogBreed dogBreed = new DogBreed(null, "2", "Pug", "Amigável e inteligente e esperto",
                10, 12, 30D, 34D, 25D, 29D, false, "Medio");
        DogBreed savedBreed = dogBreedRepository.save(dogBreed);

        HealthRecord record1 = new HealthRecord();
        record1.setCodPatient("P123");
        record1.setTutor("Aline");
        record1.setPatient("Nutella");
        record1.setBreed(savedBreed);
        record1.setAge(5.0);
        record1.setWeight(28.0);
        record1.setColor("Marrom");
        record1.setSize(DogSize.LARGE);
        record1.setGender(Gender.MALE);
        record1.setDeath(false);
        record1.setEuthanasia(Euthanasia.NO);
        record1.setAdmission(LocalDate.of(2025, 4, 11));
        HealthRecord savedRecord1 = healthRecordRepository.save(record1);

        HealthRecord record2 = new HealthRecord();
        record2.setCodPatient("P456");
        record2.setTutor("Andrea");
        record2.setPatient("Cacau");
        record2.setBreed(savedBreed);
        record2.setAge(3.0);
        record2.setWeight(26.5);
        record2.setColor("Preto");
        record2.setSize(DogSize.MEDIUM);
        record2.setGender(Gender.FEMALE);
        record2.setDeath(true);
        record2.setEuthanasia(Euthanasia.YES);
        record2.setAdmission(LocalDate.of(2025, 4, 10));
        HealthRecord savedRecord2 = healthRecordRepository.save(record2);

        when()
                .get("/health-records?page=0&qtdRecordsPage=2&sortBy=codPatient")
                .then()
                .statusCode(200)
                .body("data[0].codPatient", equalTo("P123"))
                .body("data[0].tutor", equalTo("Aline"))
                .body("data[0].patient", equalTo("Nutella"))
                .body("data[0].breedName", equalTo("Pug"))
                .body("data[0].age", equalTo(5.0F))
                .body("data[0].weight", equalTo(28.0F))
                .body("data[0].color", equalTo("Marrom"))
                .body("data[0].size", equalTo("LARGE"))
                .body("data[0].gender", equalTo("MALE"))
                .body("data[0].death", equalTo(false))
                .body("data[0].euthanasia", equalTo("NO"))
                .body("data[0].admission", equalTo("2025-04-11"))

                .body("data[1].codPatient", equalTo("P456"))
                .body("data[1].tutor", equalTo("Andrea"))
                .body("data[1].patient", equalTo("Cacau"))
                .body("data[1].breedName", equalTo("Pug"))
                .body("data[1].age", equalTo(3.0F))
                .body("data[1].weight", equalTo(26.5F))
                .body("data[1].color", equalTo("Preto"))
                .body("data[1].size", equalTo("MEDIUM"))
                .body("data[1].gender", equalTo("FEMALE"))
                .body("data[1].death", equalTo(true))
                .body("data[1].euthanasia", equalTo("YES"))
                .body("data[1].admission", equalTo("2025-04-10"));
    }

    @Test
    public void given_no_healthRecords_in_database_when_get_all_healthRecords_then_returns_noContent() {
        healthRecordRepository.deleteAll();

        when()
                .get("/health-records")
                .then()
                .statusCode(204);
    }

    @Test
    public void given_existing_healthRecordId_when_deleteHealthRecord_then_returnsNoContent() {
        DogBreed dogBreed = new DogBreed(null, "2", "Pug", "Amigável e inteligente e esperto",
                10, 12, 30D, 34D, 25D, 29D, false, "Medio");
        DogBreed savedBreed = dogBreedRepository.save(dogBreed);

        HealthRecord record = new HealthRecord();
        record.setCodPatient("P789");
        record.setTutor("Aline");
        record.setPatient("Cookie");
        record.setBreed(savedBreed);
        record.setAge(7.0);
        record.setWeight(30.0);
        record.setColor("Branco");
        record.setSize(DogSize.SMALL);
        record.setGender(Gender.FEMALE);
        record.setDeath(false);
        record.setEuthanasia(Euthanasia.NO);
        record.setAdmission(LocalDate.of(2025, 3, 15));
        HealthRecord savedRecord = healthRecordRepository.save(record);

        given()
                .pathParam("id", savedRecord.getId())
                .when()
                .delete("/health-records/{id}")
                .then()
                .statusCode(204);

        Optional<HealthRecord> deleted = healthRecordRepository.findById(savedRecord.getId());
        assertTrue(deleted.isEmpty(), "O prontuário deveria ter sido deletado do banco");
    }

    @Test
    public void given_non_existing_healthRecordId_when_deleteHealthRecord_then_returns_notFound() {
        DogBreed dogBreed = new DogBreed(null, "2", "Pug", "Amigável e inteligente e esperto",
                10, 12, 30D, 34D, 25D, 29D, false, "Medio");
        DogBreed savedBreed = dogBreedRepository.save(dogBreed);

        HealthRecord record = new HealthRecord();
        record.setCodPatient("P789");
        record.setTutor("Aline");
        record.setPatient("Cookie");
        record.setBreed(savedBreed);
        record.setAge(7.0);
        record.setWeight(30.0);
        record.setColor("Branco");
        record.setSize(DogSize.SMALL);
        record.setGender(Gender.FEMALE);
        record.setDeath(false);
        record.setEuthanasia(Euthanasia.NO);
        record.setAdmission(LocalDate.of(2025, 3, 15));
        HealthRecord savedRecord = healthRecordRepository.save(record);

        given()
                .pathParam("id", 99L)
                .when()
                .delete("/health-records/{id}")
                .then()
                .statusCode(404);

        HealthRecord existingRecord = healthRecordRepository.findById(record.getId()).orElse(null);
        assertNotNull(existingRecord, "O prontuário deveria existir no banco de dados.");
        assertEquals("Cookie", existingRecord.getPatient());
        assertEquals("Aline", existingRecord.getTutor());
    }

    @Test
    public void given_valid_id_when_getHealthRecordById_then_returns_healthRecord() {
        DogBreed dogBreed = new DogBreed(null, "2", "Pug", "Amigável e inteligente e esperto",
                10, 12, 30D, 34D, 25D, 29D, false, "Medio");
        DogBreed savedBreed = dogBreedRepository.save(dogBreed);

        HealthRecord record = new HealthRecord();
        record.setCodPatient("P789");
        record.setTutor("Aline");
        record.setPatient("Cookie");
        record.setBreed(savedBreed);
        record.setAge(7.0);
        record.setWeight(30.0);
        record.setColor("Branco");
        record.setSize(DogSize.SMALL);
        record.setGender(Gender.FEMALE);
        record.setDeath(false);
        record.setEuthanasia(Euthanasia.NO);
        record.setAdmission(LocalDate.of(2025, 3, 15));
        HealthRecord savedRecord = healthRecordRepository.save(record);

        given()
                .pathParam("id", savedRecord.getId())
                .when()
                .get("/health-records/{id}")
                .then()
                .statusCode(200)
                .body("id", equalTo(savedRecord.getId().intValue()))
                .body("patient", equalTo("Cookie"))
                .body("tutor", equalTo("Aline"))
                .body("codPatient", equalTo("P789"))
                .body("breedId", equalTo(savedBreed.getId().intValue()))
                .body("gender", equalTo("FEMALE"))
                .body("size", equalTo("SMALL"))
                .body("color", equalTo("Branco"))
                .body("age", equalTo(7.0F))
                .body("weight", equalTo(30.0F));
    }

    @Test
    public void given_non_existent_id_when_getHealthRecordById_then_returns_notFound() {
        given()
                .pathParam("id", 99L)
                .when()
                .get("/health-records/{id}")
                .then()
                .statusCode(404);
    }
}
