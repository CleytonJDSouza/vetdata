package com.project.vetdata.service;

import com.project.vetdata.dto.UserCreateDTO;
import com.project.vetdata.model.User;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

import com.project.vetdata.repository.UserRepository;
import com.project.vetdata.service.UserServiceImpl;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class UserServiceImplIT {

    @Container
    private static final MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8.0.26");

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserServiceImpl userService;

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
    public void given_valid_userCreateDTO_when_creteUser_then_returns_createdUser() {
        UserCreateDTO userCreateDTO = getFakeUserCreateDTO();
        User cretedUser = userService.createUser(userCreateDTO);

        assertNotNull(cretedUser);
        assertNotNull(cretedUser.getId());
        assertEquals(userCreateDTO.getName(), cretedUser.getName());
        assertEquals(userCreateDTO.getEmail(), cretedUser.getEmail());
        assertNotNull(cretedUser.getPassword());
    }

    @Test
    public void given_invalid_userCreateDTO_when_CreateUser_then_throws_constraintViolationException() {
        UserCreateDTO invalidUserCreateDTO = getInvalidFakeUserCreateDTO();

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<UserCreateDTO>> violations = validator.validate(invalidUserCreateDTO);

        assertFalse(violations.isEmpty(), "Deve chamar ConstraintViolationException");
    }

    @Test
    public void given_invalid_userCreateDTO_password_when_createUser_then_throws_constraintViolationException() {
        UserCreateDTO invalidUserCreateDTO = getInvalidFakePasswordUserCreateDTO();

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<UserCreateDTO>> violations = validator.validate(invalidUserCreateDTO);

        assertFalse(violations.isEmpty(), "Deve chamar ConstraintViolationException devido à senha inválida");
    }

    @Test
    public void given_existing_email_when_createUser_then_throws_dataIntegrityViolationException() {
        UserCreateDTO userCreateDTO1 = getFakeUserCreateDTO();
        userService.createUser(userCreateDTO1);

        UserCreateDTO userCreateDTO2 = getFakeUserCreateDTO2();

        assertThrows(DataIntegrityViolationException.class, () -> {
            userService.createUser(userCreateDTO2);
        });

        Optional<User> userAfter = userRepository.findByEmail(userCreateDTO1.getEmail());
        assertTrue(userAfter.isPresent(), "O primeiro usuario ainda deve estar salvo.");

        long userCount = userRepository.count();
        assertEquals(1, userCount, "Apenas um usuário deveria estar salvo no banco.");

    }

    private UserCreateDTO getFakeUserCreateDTO() {
        UserCreateDTO userCreateDTO = new UserCreateDTO();
        userCreateDTO.setName("Beatriz");
        userCreateDTO.setEmail("beatriz@vetdata.com");
        userCreateDTO.setPassword("Senha%1");
        return userCreateDTO;
    }

    private UserCreateDTO getFakeUserCreateDTO2() {
        UserCreateDTO userCreateDTO = new UserCreateDTO();
        userCreateDTO.setName("Cleyton");
        userCreateDTO.setEmail("beatriz@vetdata.com");
        userCreateDTO.setPassword("Senha%2");
        return userCreateDTO;
    }

    private UserCreateDTO getInvalidFakeUserCreateDTO() {
        UserCreateDTO userCreateDTO = new UserCreateDTO();
        userCreateDTO.setName(null);
        userCreateDTO.setEmail("beatriz@vetdata.com");
        userCreateDTO.setPassword("Senha%1");
        return userCreateDTO;
    }

    private UserCreateDTO getInvalidFakePasswordUserCreateDTO() {
        UserCreateDTO userCreateDTO = new UserCreateDTO();
        userCreateDTO.setName("Beatriz");
        userCreateDTO.setEmail("beatriz@vetdata.com");
        userCreateDTO.setPassword("senha123");
        return userCreateDTO;
    }
}

