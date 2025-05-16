package com.project.vetdata.service;

import com.password4j.Password;
import com.project.vetdata.dto.AuthenticationRequestDTO;
import com.project.vetdata.dto.AuthenticationResponseDTO;
import com.project.vetdata.enums.AuthenticationStatus;
import com.project.vetdata.model.User;
import com.project.vetdata.repository.UserRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class AuthenticationServiceImplIT {

    @Container
    private static final MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8.0.26");

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticationServiceImpl authenticationService;

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

    @BeforeEach
    void setup() {
        userRepository.deleteAll();
    }

    @Test
    void connectionEstablished() {
        assertThat(mysqlContainer.isCreated()).isTrue();
        assertThat(mysqlContainer.isRunning()).isTrue();
    }

    @Test
    public void given_valid_credentials_when_authenticate_then_return_authorized() {
        User user = new User();
        user.setName("Beatriz");
        user.setEmail("beatriz@vetdata.com");
        user.setPassword((Password.hash("Senha%1").withBcrypt().getResult()));
        user.setCreatedDate(LocalDateTime.now());
        user.setPasswordLastUpdatedDate(LocalDateTime.now());
        userRepository.save(user);

        AuthenticationRequestDTO dto = new AuthenticationRequestDTO("beatriz@vetdata.com", "Senha%1");

        AuthenticationResponseDTO response = authenticationService.authenticate(dto);

        assertNotNull(response);
        assertEquals(AuthenticationStatus.AUTHORIZED, response.status());
        assertEquals("Beatriz", response.name());
    }

    @Test
    public void given_invalid_password_when_authenticate_then_return_not_authorized() {
        User user = new User();
        user.setName("Cleyton");
        user.setEmail("cleyton@vetdata.com");
        user.setPassword((Password.hash("Senha%1").withBcrypt().getResult()));
        user.setCreatedDate(LocalDateTime.now());
        user.setPasswordLastUpdatedDate(LocalDateTime.now());
        userRepository.save(user);

        AuthenticationRequestDTO dto = new AuthenticationRequestDTO("cleyton@vetdata.com", "Senha%2");

        AuthenticationResponseDTO response = authenticationService.authenticate(dto);

        assertNotNull(response);
        assertEquals(AuthenticationStatus.NOT_AUTHORIZED, response.status());
        assertNull(response.name());
    }

    @Test
    void given_nonexistent_email_when_authenticate_then_return_not_authorized() {
        AuthenticationRequestDTO dto = new AuthenticationRequestDTO("cleyton@vetdata.com", "Senha%1");

        AuthenticationResponseDTO response = authenticationService.authenticate(dto);

        assertNotNull(response);
        assertEquals(AuthenticationStatus.NOT_AUTHORIZED, response.status());
        assertNull(response.name());
    }
}
