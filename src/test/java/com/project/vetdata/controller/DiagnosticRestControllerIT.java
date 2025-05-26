package com.project.vetdata.controller;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.project.vetdata.dto.DiagnosticCreateDTO;
import com.project.vetdata.model.Diagnostic;
import com.project.vetdata.repository.DiagnosticRepository;
import com.project.vetdata.templates.DiagnosticTemplate;
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
import java.util.List;
import java.util.Optional;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class DiagnosticRestControllerIT {

    @LocalServerPort
    private Integer port;

    @Autowired
    private DiagnosticRepository repository;

    private static final int WIREMOCK_PORT = 8082;

    private static WireMockServer wireMockServer;

    @Container
    private static final MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8.0.26");

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        repository.deleteAll();
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
    public void given_valid_diagnosticCreateDTO_when_create_diagnostic_then_returns_createdDiagnostic() {
        DiagnosticCreateDTO dto = DiagnosticTemplate.getFakeDiagnosticCreateDTO();

        Long createdUserId =
                given()
                        .contentType("application/json")
                        .body(dto)
                        .when()
                        .post("/diagnostics")
                        .then()
                        .statusCode(201)
                        .body("description", equalTo(dto.getDescription()))
                        .body("observation", equalTo(dto.getObservation()))
                        .extract()
                        .jsonPath()
                        .getLong("id");

        Optional<Diagnostic> optionalDiagnostic = repository.findById(createdUserId);
        assertTrue(optionalDiagnostic.isPresent(), "O diagnóstico não foi salvo no banco");

        Diagnostic savedDiagnostic = optionalDiagnostic.get();
        assertEquals(dto.getDescription(), savedDiagnostic.getDescription());
        assertEquals(dto.getObservation(), savedDiagnostic.getObservation());
    }

    @Test
    public void give_invalid_diagnosticCreateDTO_when_create_diagnostic_then_returns_badRequest() {
        DiagnosticCreateDTO dto = DiagnosticTemplate.getFakeDiagnosticCreateDTOWithInvalidDescription();

        given()
                .contentType("application/json")
                .body(dto)
                .when()
                .post("/diagnostics")
                .then()
                .statusCode(400);

        List<Diagnostic> diagnostics = repository.findAll();
        assertTrue(diagnostics.isEmpty(), "Nenhum diagnóstico inválido deve ser salvo no banco !");
    }

    @Test
    public void given_valid_diagnosticId_when_deleteDiagnostic_then_returns_noContent() {
        Diagnostic fakeDiagnostic = DiagnosticTemplate.getFakeDiagnostic();

        repository.save(fakeDiagnostic);

        given()
                .pathParam("id", fakeDiagnostic.getId())
                .when()
                .delete("/diagnostics/{id}")
                .then()
                .statusCode(204);

        Optional<Diagnostic> optionalDiagnostic = repository.findById(fakeDiagnostic.getId());
        assertTrue(optionalDiagnostic.isEmpty(), "O diagnostico não deve existir no banco de dados.");
    }

    @Test
    public void given_invalid_diagnosticId_when_deleteDiagnostic_then_returns_notFound() {
        Diagnostic fakeDiagnostic = DiagnosticTemplate.getFakeDiagnostic();

        repository.save(fakeDiagnostic);

        given()
                .pathParam("id", 999L)
                .when()
                .delete("/diagnostics/{id}")
                .then()
                .statusCode(404);

        Diagnostic existingDiagnostic = repository.findById(fakeDiagnostic.getId()).orElse(null);
        assertNotNull(existingDiagnostic, "O diagnóstico deveria existir no banco de dados.");
        assertEquals(fakeDiagnostic.getDescription(), existingDiagnostic.getDescription());
        assertEquals(fakeDiagnostic.getObservation(), existingDiagnostic.getObservation());
    }

    @Test
    public void given_diagnostic_exist_when_getAllDiagnostics_then_returns_list() {
        Diagnostic fakeDiagnostic = DiagnosticTemplate.getFakeDiagnostic();

        repository.save(fakeDiagnostic);

        given()
                .when()
                .get("/diagnostics")
                .then()
                .statusCode(200)
                .body("$.size()", is(1))
                .body("[0].description", equalTo(fakeDiagnostic.getDescription()))
                .body("[0].observation", equalTo(fakeDiagnostic.getObservation()));
    }

    @Test
    public void given_noDiagnostics_when_getAllDiagnostics_then_returns_emptyList() {
        repository.deleteAll();

        given()
                .when()
                .get("/diagnostics")
                .then()
                .statusCode(200)
                .body("", hasSize(0))
                .body(equalTo("[]"));
    }

    @Test
    public void given_multiple_diagnostics_when_search_without_term_then_returns_paginated_result() {
        repository.save(DiagnosticTemplate.getFakeDiagnostic());
        repository.save(DiagnosticTemplate.getFakeDiagnostic2());

        given()
                .queryParam("page", 0)
                .queryParam("qtdRecordsPage", 10)
                .queryParam("sortBy", "description")
                .when()
                .get("/diagnostics/search")
                .then()
                .statusCode(200)
                .body("total", equalTo(2))
                .body("qtdRecordsPage", equalTo(10))
                .body("page", equalTo(0))
                .body("data.size()", equalTo(2));
    }

    @Test
    public void given_diagnostics_when_search_with_matching_term_then_returns_filtered_result() {
        repository.save(DiagnosticTemplate.getFakeDiagnostic());
        repository.save(DiagnosticTemplate.getFakeDiagnostic2());

        given()
                .queryParam("searchByTerm", "gri")
                .queryParam("page", 0)
                .queryParam("qtdRecordsPage", 10)
                .when()
                .get("/diagnostics/search")
                .then()
                .statusCode(200)
                .body("total", equalTo(1))
                .body("data[0].description", equalTo("Gripe"));
    }

    @Test
    public void given_diagnostics_when_search_with_non_matching_term_then_returns_no_content() {
        repository.save(DiagnosticTemplate.getFakeDiagnostic());
        repository.save(DiagnosticTemplate.getFakeDiagnostic2());

        given()
                .queryParam("searchByTerm", "man")
                .when()
                .get("/diagnostics/search")
                .then()
                .statusCode(204);
    }
}
