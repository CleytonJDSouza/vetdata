package com.project.vetdata.controller;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.project.vetdata.dto.HospitalAdmissionCreateDTO;
import com.project.vetdata.dto.HospitalAdmissionUpdateDTO;
import com.project.vetdata.enums.*;
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
import static io.restassured.RestAssured.*;
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
                29D, false);
        DogBreed savedBreed = dogBreedRepository.save(dogBreed);

        HealthRecord healthRecord = new HealthRecord(null, 4, "P01", "Preto", false, Euthanasia.NO, Gender.MALE,
                "Torresmo", DogSize.LARGE, "Beatriz", 20.0, savedBreed);
        HealthRecord savedHealthRecord = healthRecordRepository.save(healthRecord);

        Diagnostic diagnostic = new Diagnostic("Fratura", "teste");
        Diagnostic savedDiagnostic = diagnosticRepository.save(diagnostic);

        PostOperative postOperative = new PostOperative("teste");
        PostOperative savedPostOperative = postOperativeRepository.save(postOperative);

        HospitalAdmissionCreateDTO dto = new HospitalAdmissionCreateDTO();
        dto.setDate(LocalDate.of(2025, 7, 16));
        dto.setReasonHospitalization(ReasonHospitalization.OFTALMOLOGIA);
        dto.setDateMedicalDischarge(LocalDate.of(2025, 5, 10));
        dto.setDateReturns(LocalDate.of(2025, 6, 16));
        dto.setMedicalEvolution(MedicalEvolution.QUADRO_GRAVE);
        dto.setTreatmentNextSteps(TreatmentNextSteps.CONTINUOU_TRATAMENTO);
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
        dto.setReasonHospitalization(null);
        dto.setDateMedicalDischarge(null);
        dto.setDateReturns(null);
        dto.setMedicalEvolution(null);
        dto.setTreatmentNextSteps(null);
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

    @Test
    public void given_valid_hospitalAdmissionId_and_data_valid_updateHospitalAdmission_then_returns_updateAdmission() {
        DogBreed dogBreed = new DogBreed(null, "1", "Pug", "Amigável", 10, 12, 30D, 34D, 25D,
                29D, false);
        DogBreed savedBreed = dogBreedRepository.save(dogBreed);

        HealthRecord healthRecord = new HealthRecord(null, 4, "P01", "Preto", false, Euthanasia.NO, Gender.MALE,
                "Torresmo", DogSize.LARGE, "Beatriz", 20.0, savedBreed);
        HealthRecord savedHealthRecord = healthRecordRepository.save(healthRecord);

        Diagnostic diagnostic = new Diagnostic("Fratura", "teste");
        Diagnostic savedDiagnostic = diagnosticRepository.save(diagnostic);

        PostOperative postOperative = new PostOperative("teste");
        PostOperative savedPostOperative = postOperativeRepository.save(postOperative);

        HospitalAdmission admission = new HospitalAdmission();
        admission.setDate(LocalDate.of(2025, 7, 16));
        admission.setReasonHospitalization(ReasonHospitalization.OFTALMOLOGIA);
        admission.setDateMedicalDischarge(LocalDate.of(2025, 5, 10));
        admission.setDateReturns(LocalDate.of(2025, 6, 16));
        admission.setMedicalEvolution(MedicalEvolution.QUADRO_GRAVE);
        admission.setTreatmentNextSteps(TreatmentNextSteps.CONTINUOU_TRATAMENTO);
        admission.setHealthRecord(savedHealthRecord);
        admission.setDiagnostics(Set.of(savedDiagnostic));
        admission.setPostOperatives(Set.of(savedPostOperative));

        HospitalAdmission savedAdmission = hospitalAdmissionRepository.save(admission);

        HospitalAdmissionUpdateDTO updateDTO = new HospitalAdmissionUpdateDTO();
        updateDTO.setDate(LocalDate.of(2025, 5, 1));
        updateDTO.setReasonHospitalization(ReasonHospitalization.DERMATOLOGIA);
        updateDTO.setMedicalEvolution(MedicalEvolution.PCR);
        updateDTO.setTreatmentNextSteps(TreatmentNextSteps.TUTOR_OPTOU_POR_EUTANASIA);
        updateDTO.setDateMedicalDischarge(LocalDate.of(2025, 5, 15));
        updateDTO.setDateReturns(LocalDate.of(2025, 6, 15));
        updateDTO.setHealthRecordId(savedHealthRecord.getId());
        updateDTO.setPostOperativeIds(Set.of(savedPostOperative.getId()));
        updateDTO.setDiagnosticIds(Set.of(savedDiagnostic.getId()));

        given()
                .pathParam("id", savedAdmission.getId())
                .contentType("application/json")
                .body(updateDTO)
                .when()
                .put("/hospital-admission/{id}")
                .then()
                .statusCode(200)
                .body("reasonHospitalization", equalTo(ReasonHospitalization.DERMATOLOGIA))
                .body("medicalEvolution", equalTo(MedicalEvolution.PCR))
                .body("treatmentNextSteps", equalTo(TreatmentNextSteps.TUTOR_OPTOU_POR_EUTANASIA))
                .body("date", equalTo("2025-05-01"))
                .body("dateMedicalDischarge", equalTo("2025-05-15"))
                .body("dateReturns", equalTo("2025-06-15"));

        Optional<HospitalAdmission> optional = hospitalAdmissionRepository.findWithRelationsById(savedAdmission.getId());
        assertTrue(optional.isPresent(), "A internação hospitalar atualizada não foi encontrada.");

        HospitalAdmission updatedAdmission = optional.get();
        assertEquals(LocalDate.of(2025, 5, 1), updatedAdmission.getDate());
        assertEquals(ReasonHospitalization.DERMATOLOGIA, updatedAdmission.getReasonHospitalization());
        assertEquals(MedicalEvolution.PCR, updatedAdmission.getMedicalEvolution());
        assertEquals(TreatmentNextSteps.TUTOR_OPTOU_POR_EUTANASIA, updatedAdmission.getTreatmentNextSteps());
        assertEquals(LocalDate.of(2025, 5, 15), updatedAdmission.getDateMedicalDischarge());
        assertEquals(LocalDate.of(2025, 6, 15), updatedAdmission.getDateReturns());
        assertEquals(1, updatedAdmission.getPostOperatives().size());
        assertEquals(1, updatedAdmission.getDiagnostics().size());
    }

    @Test
    public void given_invalid_hospitalAdmissionId_and_invalid_data_when_updateHospitalAdmission_then_returns_badRequest() {
        DogBreed dogBreed = new DogBreed(null, "1", "Pug", "Amigável", 10, 12, 30D, 34D, 25D,
                29D, false);
        DogBreed savedBreed = dogBreedRepository.save(dogBreed);

        HealthRecord healthRecord = new HealthRecord(null, 4, "P01", "Preto", false, Euthanasia.NO, Gender.MALE,
                "Torresmo", DogSize.LARGE, "Beatriz", 20.0, savedBreed);
        HealthRecord savedHealthRecord = healthRecordRepository.save(healthRecord);

        Diagnostic diagnostic = new Diagnostic("Fratura", "teste");
        Diagnostic savedDiagnostic = diagnosticRepository.save(diagnostic);

        PostOperative postOperative = new PostOperative("teste");
        PostOperative savedPostOperative = postOperativeRepository.save(postOperative);

        HospitalAdmission admission = new HospitalAdmission();
        admission.setDate(LocalDate.of(2025, 7, 16));
        admission.setReasonHospitalization(ReasonHospitalization.OFTALMOLOGIA);
        admission.setDateMedicalDischarge(LocalDate.of(2025, 5, 10));
        admission.setDateReturns(LocalDate.of(2025, 6, 16));
        admission.setMedicalEvolution(MedicalEvolution.QUADRO_GRAVE);
        admission.setTreatmentNextSteps(TreatmentNextSteps.TUTOR_OPTOU_POR_EUTANASIA);
        admission.setHealthRecord(savedHealthRecord);
        admission.setDiagnostics(Set.of(savedDiagnostic));
        admission.setPostOperatives(Set.of(savedPostOperative));

        HospitalAdmission savedAdmission = hospitalAdmissionRepository.save(admission);

        HospitalAdmissionUpdateDTO invalidUpdateDTO = new HospitalAdmissionUpdateDTO();
        invalidUpdateDTO.setDate(null);
        invalidUpdateDTO.setReasonHospitalization(null);
        invalidUpdateDTO.setMedicalEvolution(null);
        invalidUpdateDTO.setTreatmentNextSteps(null);
        invalidUpdateDTO.setDateMedicalDischarge(null);
        invalidUpdateDTO.setDateReturns(null);
        invalidUpdateDTO.setPostOperativeIds(null);
        invalidUpdateDTO.setDiagnosticIds(null);

        given()
                .pathParam("id", savedAdmission.getId())
                .contentType("application/json")
                .body(invalidUpdateDTO)
                .when()
                .put("/hospital-admission/{id}")
                .then()
                .statusCode(400);
    }

    @Test
    public void given_hospitalAdmissionsInDatabase_when_getAllHospitalAdmissions_then_returnsPagedList() {
        DogBreed dogBreed = new DogBreed(null, "1", "Pug", "Amigável", 10, 12, 30D, 34D, 25D,
                29D, false);
        DogBreed savedBreed = dogBreedRepository.save(dogBreed);

        HealthRecord healthRecord = new HealthRecord(null, 4, "P01", "Preto", false, Euthanasia.NO, Gender.MALE,
                "Torresmo", DogSize.LARGE, "Beatriz", 20.0, savedBreed);
        HealthRecord savedHealthRecord = healthRecordRepository.save(healthRecord);

        HealthRecord healthRecord2 = new HealthRecord(null, 3, "P02", "Branco", true, Euthanasia.YES, Gender.FEMALE,
                "Cacau", DogSize.MEDIUM, "João", 18.0, savedBreed);
        HealthRecord savedHealthRecord2 = healthRecordRepository.save(healthRecord2);

        Diagnostic diagnostic = new Diagnostic("Fratura", "teste");
        Diagnostic savedDiagnostic = diagnosticRepository.save(diagnostic);

        PostOperative postOperative = new PostOperative("teste");
        PostOperative savedPostOperative = postOperativeRepository.save(postOperative);

        HospitalAdmission admission = new HospitalAdmission();
        admission.setDate(LocalDate.of(2025, 7, 16));
        admission.setReasonHospitalization(ReasonHospitalization.ENDOCRINOLOGIA);
        admission.setDateMedicalDischarge(LocalDate.of(2025, 5, 10));
        admission.setDateReturns(LocalDate.of(2025, 6, 16));
        admission.setMedicalEvolution(MedicalEvolution.PROGRESSO_DESFAVORAVEL);
        admission.setTreatmentNextSteps(TreatmentNextSteps.EUTANASIA);
        admission.setHealthRecord(savedHealthRecord);
        admission.setDiagnostics(Set.of(savedDiagnostic));
        admission.setPostOperatives(Set.of(savedPostOperative));

        HospitalAdmission savedAdmission = hospitalAdmissionRepository.save(admission);

        HospitalAdmission admission2 = new HospitalAdmission();
        admission2.setDate(LocalDate.of(2025, 8, 10));
        admission2.setReasonHospitalization(ReasonHospitalization.OFTALMOLOGIA);
        admission2.setDateMedicalDischarge(LocalDate.of(2025, 6, 5));
        admission2.setDateReturns(LocalDate.of(2025, 7, 12));
        admission2.setMedicalEvolution(MedicalEvolution.QUADRO_GRAVE);
        admission2.setTreatmentNextSteps(TreatmentNextSteps.TUTOR_OPTOU_POR_EUTANASIA);
        admission2.setHealthRecord(savedHealthRecord2);
        admission2.setDiagnostics(Set.of(savedDiagnostic));
        admission2.setPostOperatives(Set.of(savedPostOperative));

        HospitalAdmission savedAdmission2 = hospitalAdmissionRepository.save(admission2);

        when()
                .get("/hospital-admission?page=0&qtdRecordsPage=2&sortBy=date")
                .then()
                .statusCode(200)
                .body("[0].reasonHospitalization", equalTo(ReasonHospitalization.ENDOCRINOLOGIA))
                .body("[0].medicalEvolution", equalTo(MedicalEvolution.PROGRESSO_DESFAVORAVEL))
                .body("[0].date", equalTo("2025-07-16"))
                .body("[0].dateMedicalDischarge", equalTo("2025-05-10"))
                .body("[0].dateReturns", equalTo("2025-06-16"))

                .body("[1].reasonHospitalization", equalTo(ReasonHospitalization.OFTALMOLOGIA))
                .body("[1].medicalEvolution", equalTo(MedicalEvolution.QUADRO_GRAVE))
                .body("[1].date", equalTo("2025-08-10"))
                .body("[1].dateMedicalDischarge", equalTo("2025-06-05"))
                .body("[1].dateReturns", equalTo("2025-07-12"));
    }

    @Test
    public void given_no_hospitalAdmission_in_database_when_get_all_hospitalAdmission_then_returns_noContent() {
        hospitalAdmissionRepository.deleteAll();

        when()
                .get("/health-records")
                .then()
                .statusCode(204);
    }

    @Test
    public void given_existing_hospitalAdmissionId_when_deleteHospitalAdmission_then_returnsNoContent() {
        DogBreed dogBreed = new DogBreed(null, "1", "Pug", "Amigável", 10, 12, 30D, 34D, 25D,
                29D, false);
        DogBreed savedBreed = dogBreedRepository.save(dogBreed);

        HealthRecord healthRecord = new HealthRecord(null, 4, "P01", "Preto", false, Euthanasia.NO, Gender.MALE,
                "Torresmo", DogSize.LARGE, "Beatriz", 20.0, savedBreed);
        HealthRecord savedHealthRecord = healthRecordRepository.save(healthRecord);

        Diagnostic diagnostic = new Diagnostic("Fratura", "teste");
        Diagnostic savedDiagnostic = diagnosticRepository.save(diagnostic);

        PostOperative postOperative = new PostOperative("teste");
        PostOperative savedPostOperative = postOperativeRepository.save(postOperative);

        HospitalAdmission admission = new HospitalAdmission();
        admission.setDate(LocalDate.of(2025, 7, 16));
        admission.setReasonHospitalization(ReasonHospitalization.OFTALMOLOGIA);
        admission.setDateMedicalDischarge(LocalDate.of(2025, 5, 10));
        admission.setDateReturns(LocalDate.of(2025, 6, 16));
        admission.setMedicalEvolution(MedicalEvolution.PROGRESSO_FAVORAVEL);
        admission.setTreatmentNextSteps(TreatmentNextSteps.TUTOR_INTERROMPEU_TRATAMENTO);
        admission.setHealthRecord(savedHealthRecord);
        admission.setDiagnostics(Set.of(savedDiagnostic));
        admission.setPostOperatives(Set.of(savedPostOperative));
        HospitalAdmission savedAdmission = hospitalAdmissionRepository.save(admission);

        given()
                .pathParam("id", savedAdmission.getId())
                .when()
                .delete("/hospital-admission/{id}")
                .then()
                .statusCode(204);

        Optional<HospitalAdmission> deleted = hospitalAdmissionRepository.findById(savedAdmission.getId());
        assertTrue(deleted.isEmpty(), "A internação deveria ter sido deletada do banco");
    }

    @Test
    public void given_non_existing_hospitalAdmissionId_when_deleteHospitalAdmission_then_returns_notFound() {
        DogBreed dogBreed = new DogBreed(null, "1", "Pug", "Amigável", 10, 12, 30D, 34D, 25D,
                29D, false);
        DogBreed savedBreed = dogBreedRepository.save(dogBreed);

        HealthRecord healthRecord = new HealthRecord(null, 4, "P01", "Preto", false, Euthanasia.NO, Gender.MALE,
                "Torresmo", DogSize.LARGE, "Beatriz", 20.0, savedBreed);
        HealthRecord savedHealthRecord = healthRecordRepository.save(healthRecord);

        Diagnostic diagnostic = new Diagnostic("Fratura", "teste");
        Diagnostic savedDiagnostic = diagnosticRepository.save(diagnostic);

        PostOperative postOperative = new PostOperative("teste");
        PostOperative savedPostOperative = postOperativeRepository.save(postOperative);

        HospitalAdmission admission = new HospitalAdmission();
        admission.setDate(LocalDate.of(2025, 7, 16));
        admission.setReasonHospitalization(ReasonHospitalization.NEFROLOGIA);
        admission.setDateMedicalDischarge(LocalDate.of(2025, 5, 10));
        admission.setDateReturns(LocalDate.of(2025, 6, 16));
        admission.setMedicalEvolution(MedicalEvolution.PROGRESSO_FAVORAVEL);
        admission.setTreatmentNextSteps(TreatmentNextSteps.CONTINUOU_TRATAMENTO);
        admission.setHealthRecord(savedHealthRecord);
        admission.setDiagnostics(Set.of(savedDiagnostic));
        admission.setPostOperatives(Set.of(savedPostOperative));
        HospitalAdmission savedAdmission = hospitalAdmissionRepository.save(admission);

        given()
                .pathParam("id", 99L)
                .when()
                .delete("/hospital-admission/{id}")
                .then()
                .statusCode(404);

        HospitalAdmission existingAdmission = hospitalAdmissionRepository.findById(admission.getId()).orElse(null);
        assertNotNull(existingAdmission, "A internação deveria existir no bancko de dados.");
        assertEquals(ReasonHospitalization.NEFROLOGIA, existingAdmission.getReasonHospitalization());
        assertEquals(MedicalEvolution.PROGRESSO_FAVORAVEL, existingAdmission.getMedicalEvolution());
        assertEquals(TreatmentNextSteps.CONTINUOU_TRATAMENTO, existingAdmission.getTreatmentNextSteps());
    }

    @Test
    public void given_valid_id_when_getHospitalAdmissionById_then_returns_hospitalAdmission() {
        DogBreed dogBreed = new DogBreed(null, "1", "Pug", "Amigável", 10, 12, 30D, 34D, 25D,
                29D, false);
        DogBreed savedBreed = dogBreedRepository.save(dogBreed);

        HealthRecord healthRecord = new HealthRecord(null, 4, "P01", "Preto", false, Euthanasia.NO, Gender.MALE,
                "Torresmo", DogSize.LARGE, "Beatriz", 20.0, savedBreed);
        HealthRecord savedHealthRecord = healthRecordRepository.save(healthRecord);

        Diagnostic diagnostic = new Diagnostic("Fratura", "teste");
        Diagnostic savedDiagnostic = diagnosticRepository.save(diagnostic);

        PostOperative postOperative = new PostOperative("teste");
        PostOperative savedPostOperative = postOperativeRepository.save(postOperative);

        HospitalAdmission admission = new HospitalAdmission();
        admission.setDate(LocalDate.of(2025, 7, 16));
        admission.setReasonHospitalization(ReasonHospitalization.NEFROLOGIA);
        admission.setDateMedicalDischarge(LocalDate.of(2025, 5, 10));
        admission.setDateReturns(LocalDate.of(2025, 6, 16));
        admission.setMedicalEvolution(MedicalEvolution.PROGRESSO_FAVORAVEL);
        admission.setTreatmentNextSteps(TreatmentNextSteps.CONTINUOU_TRATAMENTO);
        admission.setHealthRecord(savedHealthRecord);
        admission.setDiagnostics(Set.of(savedDiagnostic));
        admission.setPostOperatives(Set.of(savedPostOperative));
        HospitalAdmission savedAdmission = hospitalAdmissionRepository.save(admission);

        given()
                .pathParam("id", savedAdmission.getId())
                .when()
                .get("/hospital-admission/{id}")
                .then()
                .statusCode(200)
                .body("id", equalTo(savedAdmission.getId().intValue()))
                .body("treatmentNextSteps", equalTo(TreatmentNextSteps.CONTINUOU_TRATAMENTO))
                .body("medicalEvolution", equalTo(MedicalEvolution.PROGRESSO_FAVORAVEL))
                .body("date", equalTo("2025-07-16"))
                .body("dateMedicalDischarge", equalTo("2025-05-10"))
                .body("dateReturns", equalTo("2025-06-16"));
    }

    @Test
    public void given_non_existent_id_when_getHospitalAdmissionById_then_returns_notFound() {
        given()
                .pathParam("id", 99L)
                .when()
                .get("/hospital-admission/{id}")
                .then()
                .statusCode(404);
    }
}
