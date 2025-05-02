package com.project.vetdata.controller;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.project.vetdata.dto.HospitalAdmissionCreateDTO;
import com.project.vetdata.enums.DogSize;
import com.project.vetdata.enums.Euthanasia;
import com.project.vetdata.enums.Gender;
import com.project.vetdata.model.*;
import com.project.vetdata.repository.*;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
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
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class HospitalAdmissionControllerIT {

    @LocalServerPort
    private Integer port;

    @Autowired
    private HospitalAdmissionRepository hospitalAdmissionRepository;

    @Autowired
    private HealthRecordRepository healthRecordRepository;

    @Autowired
    private DiagnosticRepository diagnosticRepository;

    @Autowired
    private PostOperativeRepository postOperativeRepository;

    @Autowired
    private DogBreedRepository dogBreedRepository;

    private static final int WIREMOCK_PORT = 8082;

    private static WireMockServer wireMockServer;

    @Container
    private static final MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8.0.26");

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        hospitalAdmissionRepository.deleteAll();
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
    public void given_valid_hospitalAdmissionCreateDTO_when_createHospitalAdmission_then_returns_created() {
        DogBreed dogBreed = new DogBreed(null, "1", "Pug", "Amigável", 10, 12, 30D, 34D, 25D,
                29D, false, "Grande");
        DogBreed savedBreed = dogBreedRepository.save(dogBreed);

        HealthRecord healthRecord = new HealthRecord(null, LocalDate.of(2025, 1, 16), 4.0, "P01", "Preto", false, Euthanasia.NO, Gender.MALE,
                "Torresmo", DogSize.LARGE, "Beatriz", 20.0, savedBreed);
        HealthRecord savedHealthRecord = healthRecordRepository.save(healthRecord);

        Diagnostic diagnostic = new Diagnostic("Fratura", "teste");
        Diagnostic savedDiagnostic = diagnosticRepository.save(diagnostic);

        PostOperative postOperative = new PostOperative("teste");
        PostOperative savedPostOperative = postOperativeRepository.save(postOperative);

        HospitalAdmissionCreateDTO dto = new HospitalAdmissionCreateDTO();
        dto.setDate(LocalDate.of(2025, 7, 16));
        dto.setReasonHospitalization("Tratamento");
        dto.setDateMedicalDischarge(LocalDate.of(2025, 5, 10));
        dto.setDateReturns(LocalDate.of(2025, 6, 16));
        dto.setMedicalEvolution("Recuperando");
        dto.setTreatmentNextSteps("Retorno");
        dto.setHealthRecordId(savedHealthRecord.getId());
        dto.setPostOperativeIds(Set.of(savedPostOperative.getId()));
        dto.setDiagnosticIds(Set.of(savedDiagnostic.getId()));

        Long createdId =
                given()
                        .contentType("application/json")
                        .body(dto)
                        .when()
                        .post("/hospital-admission")
                        .then()
                        .statusCode(201)
                        .body("reasonHospitalization", equalTo(dto.getReasonHospitalization()))
                        .body("medicalEvolution", equalTo(dto.getMedicalEvolution()))
                        .body("treatmentNextSteps", equalTo(dto.getTreatmentNextSteps()))
                        .extract()
                        .jsonPath()
                        .getLong("id");

        Optional<HospitalAdmission> optional = hospitalAdmissionRepository.findWithRelationsById(createdId);
        assertTrue(optional.isPresent(), "A internação hospitalar não foi salva no banco");

        HospitalAdmission saved = optional.get();
        assertEquals(dto.getDate(), saved.getDate());
        assertEquals(dto.getReasonHospitalization(), saved.getReasonHospitalization());
        assertEquals(dto.getDateMedicalDischarge(), saved.getDateMedicalDischarge());
        assertEquals(dto.getDateReturns(), saved.getDateReturns());
        assertEquals(dto.getMedicalEvolution(), saved.getMedicalEvolution());
        assertEquals(dto.getTreatmentNextSteps(), saved.getTreatmentNextSteps());
        assertEquals(dto.getHealthRecordId(), saved.getHealthRecord().getId());
        assertEquals(1, saved.getPostOperatives().size());
        assertEquals(1, saved.getDiagnostics().size());
    }

    @Test
    public void given_invalid_hospitalAdmissionCreateDTO_when_createHospitalAdmission_then_returns_badRequest() {
        HospitalAdmissionCreateDTO dto = new HospitalAdmissionCreateDTO();
        dto.setDate(null);
        dto.setReasonHospitalization("");
        dto.setDateMedicalDischarge(null);
        dto.setDateReturns(null);
        dto.setMedicalEvolution("");
        dto.setTreatmentNextSteps("");
        dto.setHealthRecordId(null);
        dto.setPostOperativeIds(null);
        dto.setDiagnosticIds(null);

        given()
                .contentType("application/json")
                .body(dto)
                .when()
                .post("/hospital-admission")
                .then()
                .statusCode(400);

        List<HospitalAdmission> admissions = hospitalAdmissionRepository.findAll();
        assertTrue(admissions.isEmpty(), "Nenhuma internação deve ser salva.");
    }
}
