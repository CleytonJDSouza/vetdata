package com.project.vetdata.service;

import com.project.vetdata.dto.DiagnosticCreateDTO;
import com.project.vetdata.model.Diagnostic;
import com.project.vetdata.templates.DiagnosticTemplate;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class DiagnosticServiceIT {

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
    private DiagnosticService service;

    @Test
    void connectionEstablished() {
        assertThat(mysqlContainer.isCreated()).isTrue();
        assertThat(mysqlContainer.isRunning()).isTrue();
    }

    @Test
    public void given_valid_diagnosticCreateDTO_when_creteDiagnostic_then_returns_createdDiagnostic() {
        DiagnosticCreateDTO dto = DiagnosticTemplate.getFakeDiagnosticCreateDTO();
        Diagnostic cretedDiagnostic = service.createDiagnostic(dto);

        assertNotNull(cretedDiagnostic);
        assertNotNull(cretedDiagnostic.getId());
        assertEquals(dto.getDescription(), cretedDiagnostic.getDescription());
        assertEquals(dto.getObservation(), dto.getObservation());
    }

    @Test
    public void given_invalid_diagnosticCreateDTO_when_CreateDiagnostic_then_throws_constraintViolationException() {
        DiagnosticCreateDTO invalidDiagnostic = DiagnosticTemplate.getFakeDiagnosticCreateDTOWithInvalidDescription();

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<DiagnosticCreateDTO>> violations = validator.validate(invalidDiagnostic);

        assertFalse(violations.isEmpty(), "Deve chamar ConstraintViolationException");
    }

    @Test
    public void given_valid_id_when_deleteDiagnostic_then_diagnostic_is_deleted() {
        DiagnosticCreateDTO dto = DiagnosticTemplate.getFakeDiagnosticCreateDTO();
        Diagnostic createDiagnostic = service.createDiagnostic(dto);

        assertNotNull(createDiagnostic.getId(), "Conferir se o diagnóstico foi salvo");

        Long id = createDiagnostic.getId();
        service.deleteDiagnostic(id);

        Optional<Diagnostic> deletedDiagnostic = service.getDiagnosticById(id);
        assertFalse(deletedDiagnostic.isPresent(), "O diagnóstico deve ter sido deletado");
    }

    @Test
    public void given_no_diagnostics_when_getAllDiagnostics_then_return_empty_list() {
        List<Diagnostic> result = service.getAllDiagnostics();

        assertNotNull(result, "A lista não deve ser nula");
        assertTrue(result.isEmpty(),"A lista deve estar vazia");
    }

    @Test
    public void given_existing_diagnostics_when_getAllDiagnostics_then_return_list_of_diagnostics() {
        DiagnosticCreateDTO dto = DiagnosticTemplate.getFakeDiagnosticCreateDTO();

        service.createDiagnostic(dto);

        List<Diagnostic> result = service.getAllDiagnostics();

        assertNotNull(result, "A lista retornar não deve ser nula");
        assertEquals(1, result.size(),"Deve ter 2 diagnóstic salvos");

        List<String> names = result.stream().map(Diagnostic::getDescription).toList();
        assertTrue(names.contains("Cancer"));
    }
}
