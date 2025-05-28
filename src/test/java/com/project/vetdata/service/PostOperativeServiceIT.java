package com.project.vetdata.service;

import com.project.vetdata.dto.PostOperativeCreateDTO;
import com.project.vetdata.model.PostOperative;
import com.project.vetdata.repository.PostOperativeRepository;
import com.project.vetdata.templates.PostOperativeTemplate;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class PostOperativeServiceIT {

    @Container
    private static final MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8.0.26");

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

    @Autowired
    private PostOperativeService service;

    @Autowired
    private PostOperativeRepository repository;

    @BeforeEach
    public void setup(){
        repository.deleteAll();
    }

    @Test
    void connectionEstablished() {
        assertThat(mysqlContainer.isCreated()).isTrue();
        assertThat(mysqlContainer.isRunning()).isTrue();
    }

    @Test
    public void given_valid_postOperativeCreateDTO_when_cretePostOperative_then_returns_createdPostOperative() {
        PostOperativeCreateDTO dto = PostOperativeTemplate.getFakePostOperativeCreateDTO();
        PostOperative cretedDiagnostic = service.createPostOperative(dto);

        assertNotNull(cretedDiagnostic);
        assertNotNull(cretedDiagnostic.getId());
        assertEquals(dto.getDescription(), cretedDiagnostic.getDescription());
    }

    @Test
    public void given_invalid_postOperativeCreateDTO_when_CreatePostOperative_then_throws_constraintViolationException() {
        PostOperativeCreateDTO invalidDiagnostic = PostOperativeTemplate.getFakePostOperativeCreateDTOWithInvalidDescription();

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<PostOperativeCreateDTO>> violations = validator.validate(invalidDiagnostic);

        assertFalse(violations.isEmpty(), "Deve chamar ConstraintViolationException");
    }

    @Test
    public void given_valid_id_when_deletePostOperative_then_postOperative_is_deleted() {
        PostOperativeCreateDTO dto = PostOperativeTemplate.getFakePostOperativeCreateDTO();
        PostOperative createDiagnostic = service.createPostOperative(dto);

        assertNotNull(createDiagnostic.getId(), "Conferir se o diagnóstico foi salvo");

        Long id = createDiagnostic.getId();
        service.deletePostOperative(id);

        Optional<PostOperative> deletedDiagnostic = service.getPostOperativeById(id);
        assertFalse(deletedDiagnostic.isPresent(), "O diagnóstico deve ter sido deletado");
    }

    @Test
    public void given_no_postOperative_when_getAllPostOperatives_then_return_empty_list() {
        List<PostOperative> result = service.getAllPostOperatives();

        assertNotNull(result, "A lista não deve ser nula");
        assertTrue(result.isEmpty(),"A lista deve estar vazia");
    }

    @Test
    public void given_existing_postOperatives_when_getAllPostOperatives_then_return_list_of_postOperatives() {
        PostOperativeCreateDTO dto = PostOperativeTemplate.getFakePostOperativeCreateDTO();

        service.createPostOperative(dto);

        List<PostOperative> result = service.getAllPostOperatives();

        assertNotNull(result, "A lista retornar não deve ser nula");
        assertEquals(1, result.size(),"Deve ter 2 diagnóstic salvos");

        List<String> names = result.stream().map(PostOperative::getDescription).toList();
        assertTrue(names.contains("Fisioterapia"));
    }

    @Test
    public void given_multiple_postOperatives_when_getAllPostOperativesPaginated_then_return_paginated_postOperatives() {
        service.createPostOperative(PostOperativeTemplate.getFakePostOperativeCreateDTO());
        service.createPostOperative(PostOperativeTemplate.getFakePostOperativeCreateDTO2());

        Pageable pageable = PageRequest.of(0, 10, Sort.by("description"));
        Page<PostOperative> result = service.getAllPostOperativesPaginated(pageable);

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertFalse(result.isEmpty(), "A página não deve estar vazia");
    }

    @Test
    public void given_postOperatives_when_getBySearchTerm_with_matching_term_then_return_filtered_page() {
        service.createPostOperative(PostOperativeTemplate.getFakePostOperativeCreateDTO());
        service.createPostOperative(PostOperativeTemplate.getFakePostOperativeCreateDTO2());

        Pageable pageable = PageRequest.of(0, 10);
        Page<PostOperative> result = service.getBySearchTerm("fisiote", pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertTrue(result.getContent().get(0).getDescription().toLowerCase().contains("fisiote"));
    }

    @Test
    public void given_postOperatives_when_getBySearchTerm_with_no_match_then_return_empty_page() {
        service.createPostOperative(PostOperativeTemplate.getFakePostOperativeCreateDTO());
        service.createPostOperative(PostOperativeTemplate.getFakePostOperativeCreateDTO2());

        Pageable pageable = PageRequest.of(0, 10);
        Page<PostOperative> result = service.getBySearchTerm("Man", pageable);

        assertNotNull(result);
        assertTrue(result.isEmpty(), "A página deve estar vazia");
    }
}
