package com.project.vetdata.controller;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.password4j.Password;
import com.project.vetdata.dto.AuthenticationRequestDTO;
import com.project.vetdata.model.User;
import com.project.vetdata.repository.UserRepository;
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

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

import static org.hamcrest.Matchers.*;

import java.time.LocalDateTime;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class AuthenticationRestControllerIT {

    @LocalServerPort
    private Integer port;

    @Autowired
    private UserRepository userRepository;

    private static final int WIREMOCK_PORT = 8082;

    private static WireMockServer wireMockServer;

    @Container
    private static final MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8.0.26");

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        userRepository.deleteAll();
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
    public void given_valid_credentials_when_login_then_returns_authorized() {
        User user = new User();
        user.setName("Aline");
        user.setEmail("aline@vetdata.com");
        user.setPassword(Password.hash("Senha%1").withBcrypt().getResult());
        user.setCreatedDate(LocalDateTime.now());
        user.setPasswordLastUpdatedDate(LocalDateTime.now());
        userRepository.save(user);

        AuthenticationRequestDTO request = new AuthenticationRequestDTO("aline@vetdata.com", "Senha%1");

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/authentications")
                .then()
                .statusCode(200)
                .body("status", equalTo("AUTHORIZED"))
                .body("name", equalTo("Aline"));
    }

    @Test
    public void given_invalid_email_when_login_then_returns_not_authorized() {
        AuthenticationRequestDTO request = new AuthenticationRequestDTO("cleyton@vetdata.com", "Senha%1");

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/authentications")
                .then()
                .statusCode(200)
                .body("status", equalTo("NOT_AUTHORIZED"))
                .body("name", nullValue());
    }

    @Test
    public void given_invalid_password_when_login_then_returns_not_authorized() {
        User user = new User();
        user.setName("Aline");
        user.setEmail("aline@vetdata.com");
        user.setPassword(Password.hash("Senha%1").withBcrypt().getResult());
        user.setCreatedDate(LocalDateTime.now());
        user.setPasswordLastUpdatedDate(LocalDateTime.now());
        userRepository.save(user);

        AuthenticationRequestDTO request = new AuthenticationRequestDTO("aline@vetdata.com", "Senha%2");

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/authentications")
                .then()
                .statusCode(200)
                .body("status", equalTo("NOT_AUTHORIZED"))
                .body("name", nullValue());
    }

    @Test
    public void given_blank_fields_when_login_then_returns_400() {
        AuthenticationRequestDTO request = new AuthenticationRequestDTO("", "");

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/authentications")
                .then()
                .statusCode(400);
    }
}
