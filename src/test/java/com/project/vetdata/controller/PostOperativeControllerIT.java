package com.project.vetdata.controller;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.project.vetdata.dto.DiagnosticCreateDTO;
import com.project.vetdata.dto.PostOperativeCreateDTO;
import com.project.vetdata.model.Diagnostic;
import com.project.vetdata.model.PostOperative;
import com.project.vetdata.repository.DiagnosticRepository;
import com.project.vetdata.repository.PostOperativeRepository;
import com.project.vetdata.templates.DiagnosticTemplate;
import com.project.vetdata.templates.PostOperativeTemplate;
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
public class PostOperativeControllerIT {

    @LocalServerPort
    private Integer port;

    @Autowired
    private PostOperativeRepository repository;

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
    public void given_valid_postOperativeCreateDTO_when_create_postOperative_then_returns_createdPostOperative() {
        PostOperativeCreateDTO dto = PostOperativeTemplate.getFakePostOperativeCreateDTO();

        Long createdUserId =
                given()
                        .contentType("application/json")
                        .body(dto)
                        .when()
                        .post("/post-operatives")
                        .then()
                        .statusCode(201)
                        .body("description", equalTo(dto.getDescription()))
                        .extract()
                        .jsonPath()
                        .getLong("id");

        Optional<PostOperative> optionalPostOperative = repository.findById(createdUserId);
        assertTrue(optionalPostOperative.isPresent(), "O pós operatório não foi salvo no banco");

        PostOperative savedPostOperative = optionalPostOperative.get();
        assertEquals(dto.getDescription(), savedPostOperative.getDescription());
    }

    @Test
    public void give_invalid_postOperativeCreateDTO_when_create_postOperative_then_returns_badRequest() {
        PostOperativeCreateDTO dto = PostOperativeTemplate.getFakePostOperativeCreateDTOWithInvalidDescription();

        given()
                .contentType("application/json")
                .body(dto)
                .when()
                .post("/post-operatives")
                .then()
                .statusCode(400);

        List<PostOperative> postOperatives = repository.findAll();
        assertTrue(postOperatives.isEmpty(), "Nenhum pós operatório inválido deve ser salvo no banco !");
    }

    @Test
    public void given_valid_postOperativeId_when_deletePostOperative_then_returns_noContent() {
        PostOperative fakePostOperative = PostOperativeTemplate.getFakePostOperative();

        repository.save(fakePostOperative);

        given()
                .pathParam("id", fakePostOperative.getId())
                .when()
                .delete("/post-operatives/{id}")
                .then()
                .statusCode(204);

        Optional<PostOperative> optionalPostOperative = repository.findById(fakePostOperative.getId());
        assertTrue(optionalPostOperative.isEmpty(), "O pós operatório não deve existir no banco de dados.");
    }

    @Test
    public void given_invalid_postOperativeId_when_deletePostOperative_then_returns_notFound() {
        PostOperative fakePostOperative = PostOperativeTemplate.getFakePostOperative();

        repository.save(fakePostOperative);

        given()
                .pathParam("id", 999L)
                .when()
                .delete("/post-operatives/{id}")
                .then()
                .statusCode(404);

        PostOperative existingPostOperative = repository.findById(fakePostOperative.getId()).orElse(null);
        assertNotNull(existingPostOperative, "O pós operatório deveria existir no banco de dados.");
        assertEquals(fakePostOperative.getDescription(), existingPostOperative.getDescription());
    }

    @Test
    public void given_postOperative_exist_when_getAllPostOperatives_then_returns_list() {
        PostOperative fakePostOperative = PostOperativeTemplate.getFakePostOperative();

        repository.save(fakePostOperative);

        given()
                .when()
                .get("/post-operatives")
                .then()
                .statusCode(200)
                .body("$.size()", is(1))
                .body("[0].description", equalTo(fakePostOperative.getDescription()));
    }

    @Test
    public void given_noPostOperative_when_getAllPostOperatives_then_returns_emptyList() {
        repository.deleteAll();

        given()
                .when()
                .get("/post-operatives")
                .then()
                .statusCode(200)
                .body("", hasSize(0))
                .body(equalTo("[]"));
    }
}
