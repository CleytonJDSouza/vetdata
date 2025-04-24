package com.project.vetdata.controller;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.password4j.Password;
import com.project.vetdata.dto.UserCreateDTO;
import com.project.vetdata.model.User;
import com.project.vetdata.repository.UserRepository;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class UserRestControllerIT {

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
    public void given_valid_userCreateDTO_when_create_user_then_returns_createdUser() {
        UserCreateDTO userCreateDTO = new UserCreateDTO();
        userCreateDTO.setName("Beatriz");
        userCreateDTO.setEmail("beatriz@vetdata.com");
        userCreateDTO.setPassword("Teste%1");

        Long createdUserId =
                given()
                        .contentType("application/json")
                        .body(userCreateDTO)
                        .when()
                        .post("/users")
                        .then()
                        .statusCode(201)
                        .body("name", equalTo(userCreateDTO.getName()))
                        .body("email", equalTo(userCreateDTO.getEmail()))
                        .extract()
                        .jsonPath()
                        .getLong("id");

        Optional<User> optionalUser = userRepository.findById(createdUserId);
        assertTrue(optionalUser.isPresent(), "O usuário não foi salvo no banco");

        User savedUser = optionalUser.get();
        assertEquals(userCreateDTO.getName(), savedUser.getName());
        assertEquals(userCreateDTO.getEmail(), savedUser.getEmail());
        assertNotNull(savedUser.getCreatedDate(), "A data não foi preenchida");
        assertNotNull(savedUser.getPasswordLastUpdatedDate(), "A data de altualização não foi preenchida");

        assertNotEquals(userCreateDTO.getPassword(), savedUser.getPassword(), "A senha não deveria estar armazenada em texto puro");
        assertTrue(Password.check(userCreateDTO.getPassword(), savedUser.getPassword()).withBcrypt(),
                "A senha não foi corretamente criptografada");
    }

    @Test
    public void give_invalid_userCreateDTO_when_create_user_then_returns_badRequest() {
        UserCreateDTO userCreateDTO = new UserCreateDTO();
        userCreateDTO.setName("");
        userCreateDTO.setEmail("beatriz-vetdata");
        userCreateDTO.setPassword("123");

        given()
                .contentType("application/json")
                .body(userCreateDTO)
                .when()
                .post("/users")
                .then()
                .statusCode(400);

        List<User> users = userRepository.findAll();
        assertTrue(users.isEmpty(), "Nenhuma raça pode estar salva");
    }

    @Test
    public void given_valid_userId_when_deleteUser_then_returns_noContent() {
        User user = new User();
        user.setName("Beatriz");
        user.setEmail("beatriz@vetdata.com");
        user.setPassword(Password.hash("Senha%1").withBcrypt().getResult());
        user.setCreatedDate(LocalDateTime.now());
        user.setPasswordLastUpdatedDate(LocalDateTime.now());

        user = userRepository.save(user);

        given()
                .pathParam("id", user.getId())
                .when()
                .delete("/users/{id}")
                .then()
                .statusCode(204);

        Optional<User> optionalUser = userRepository.findById(user.getId());
        assertTrue(optionalUser.isEmpty(), "O usuário ainda existe no banco de dados.");
    }

    @Test
    public void given_invalid_userId_when_deleteUser_then_returns_notFound() {
        User user = new User();
        user.setName("Cleyton");
        user.setEmail("cleyton@vetdata.com");
        user.setPassword(Password.hash("Teste%123").withBcrypt().getResult());
        user.setCreatedDate(LocalDateTime.now());
        user.setPasswordLastUpdatedDate(LocalDateTime.now());

        userRepository.save(user);

        given()
                .pathParam("id", 999L)
                .when()
                .delete("/users/{id}")
                .then()
                .statusCode(404);

        User existingUser = userRepository.findById(user.getId()).orElse(null);
        assertNotNull(existingUser, "O usuário deveria existir no banco de dados.");
        assertEquals("Cleyton", existingUser.getName());
        assertEquals("cleyton@vetdata.com", existingUser.getEmail());
    }

    @Test
    public void given_users_exist_when_getAllUsers_then_returns_list() {
        User user1 = new User();
        user1.setName("Beatriz");
        user1.setEmail("beatriz@vetdata.com");
        user1.setPassword(Password.hash("Senha%1").withBcrypt().getResult());
        user1.setCreatedDate(LocalDateTime.now());
        user1.setPasswordLastUpdatedDate(LocalDateTime.now());

        User user2 = new User();
        user2.setName("Cleyton");
        user2.setEmail("cleyton@vetdata.com");
        user2.setPassword(Password.hash("Senha%2").withBcrypt().getResult());
        user2.setCreatedDate(LocalDateTime.now());
        user2.setPasswordLastUpdatedDate(LocalDateTime.now());

        userRepository.saveAll(List.of(user1, user2));

        given()
                .when()
                .get("/users")
                .then()
                .statusCode(200)
                .body("$.size()", is(2))
                .body("[0].name", equalTo("Beatriz"))
                .body("[0].email", equalTo("beatriz@vetdata.com"))
                .body("[1].name", equalTo("Cleyton"))
                .body("[1].email", equalTo("cleyton@vetdata.com"));
    }

    @Test
    public void given_noUsers_when_getAllUsers_then_returns_emptyList() {
        userRepository.deleteAll();

        given()
                .when()
                .get("/users")
                .then()
                .statusCode(200)
                .body("", hasSize(0))
                .body(equalTo("[]"));
    }
}
