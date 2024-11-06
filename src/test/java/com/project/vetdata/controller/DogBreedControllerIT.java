package com.project.vetdata.controller;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.project.vetdata.dto.DogBreedCreateDTO;
import com.project.vetdata.dto.DogBreedUpdateDTO;
import com.project.vetdata.model.DogBreed;
import com.project.vetdata.repository.DogBreedRepository;
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

import javax.swing.text.html.Option;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.number.OrderingComparison.greaterThan;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static com.github.tomakehurst.wiremock.client.WireMock.*;


@SpringBootTest(webEnvironment = RANDOM_PORT)
@Testcontainers
public class DogBreedControllerIT {

    @LocalServerPort
    private Integer port;

    @Autowired
    private DogBreedRepository dogBreedRepository;

    private static final int WIREMOCK_PORT = 8082;

    private static WireMockServer wireMockServer;

    @Container
    private static final MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8.0.26");

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
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

    private String loadPayload(String filePath) throws Exception {
        return new String(Files.readAllBytes(Paths.get(filePath)));
    }


    @Test
    public void given_validDog_BreedId_when_getBreedById_then_returns_dogBreed() {
        DogBreed dogBreed = new DogBreed();
        dogBreed.setId(1L);
        dogBreed.setName("Tanjiro");
        dogBreed.setDescription("Respiracao da agua querta forma");
        dogBreed.setLifeExpectancyMin(10);
        dogBreed.setLifeExpectancyMax(14);
        dogBreed.setMaleWeightMin(12D);
        dogBreed.setMaleWeightMax(16D);
        dogBreed.setFemaleWeightMin(11D);
        dogBreed.setFemaleWeightMax(15D);
        dogBreed.setHypoallergenic(true);
        dogBreed.setSize("Pequeno");
        DogBreed save = dogBreedRepository.save(dogBreed);
        when()
                .get("/breeds/{id}", 1L)
                .then()
                .statusCode(200)
                .body("id", equalTo(1))
                .body("name", equalTo(save.getName()))
                .body("description", equalTo(save.getDescription()))
                .body("lifeExpectancyMin", equalTo(save.getLifeExpectancyMin()))
                .body("lifeExpectancyMax", equalTo(save.getLifeExpectancyMax()))
                .body("maleWeightMin", equalTo(12.0F))
                .body("maleWeightMax", equalTo(16.0F))
                .body("femaleWeightMin", equalTo(11.0F))
                .body("femaleWeightMax", equalTo(15.0F))
                .body("hypoallergenic", equalTo(save.getHypoallergenic()))
                .body("size", equalTo(save.getSize()));
    }

    @Test
    public void given_non_existing_dogBreedId_when_getBreedById_then_returns_notFound() {
        when()
                .get("/breeds/{id}", 999L)
                .then()
                .statusCode(404);
    }

    @Test
    public void given_invalid_dogBreedId_when_getBreedById_then_returns_badRequest() {
        when()
                .get("/breeds/{id}", "abc")
                .then()
                .statusCode(400);
    }

    @Test
    public void given_dogBreeds_in_dataBase_when_getAllDogBreeds_then_returns_pagedList() {
        DogBreed dogBreed = new DogBreed();
        dogBreed.setId(11L);
        dogBreed.setName("Tanjiro");
        dogBreed.setDescription("Respiracao da agua querta forma");
        dogBreed.setLifeExpectancyMin(10);
        dogBreed.setLifeExpectancyMax(14);
        dogBreed.setMaleWeightMin(12D);
        dogBreed.setMaleWeightMax(16D);
        dogBreed.setFemaleWeightMin(11D);
        dogBreed.setFemaleWeightMax(15D);
        dogBreed.setHypoallergenic(true);
        dogBreed.setSize("Pequeno");

        DogBreed dogBreed2 = new DogBreed();
        dogBreed2.setId(12L);
        dogBreed2.setName("Zenitsu");
        dogBreed2.setDescription("ZZZZZZZ");
        dogBreed2.setLifeExpectancyMin(10);
        dogBreed2.setLifeExpectancyMax(14);
        dogBreed2.setMaleWeightMin(12D);
        dogBreed2.setMaleWeightMax(16D);
        dogBreed2.setFemaleWeightMin(11D);
        dogBreed2.setFemaleWeightMax(15D);
        dogBreed2.setHypoallergenic(false);
        dogBreed2.setSize("Médio");

        DogBreed save = dogBreedRepository.save(dogBreed);
        DogBreed save2 = dogBreedRepository.save(dogBreed2);

        when()
                .get("/breeds?page=0&qtdRecordsPage=2&sortBy=name")
                .then()
                .statusCode(200)
                .body("data[0].id", greaterThan(0))
                .body("data[0].name", equalTo("Tanjiro"))
                .body("data[0].description", equalTo("Respiracao da agua querta forma"))
                .body("data[0].lifeExpectancyMin", equalTo(10))
                .body("data[0].lifeExpectancyMax", equalTo(14))
                .body("data[0].maleWeightMin", equalTo(12.0F))
                .body("data[0].maleWeightMax", equalTo(16.0F))
                .body("data[0].femaleWeightMin", equalTo(11.0F))
                .body("data[0].femaleWeightMax", equalTo(15.0F))
                .body("data[0].hypoallergenic", equalTo(true))
                .body("data[0].size", equalTo("Pequeno"))

                .body("data[1].id", greaterThan(0))
                .body("data[1].name", equalTo("Zenitsu"))
                .body("data[1].description", equalTo("ZZZZZZZ"))
                .body("data[1].lifeExpectancyMin", equalTo(10))
                .body("data[1].lifeExpectancyMax", equalTo(14))
                .body("data[1].maleWeightMin", equalTo(12.0F))
                .body("data[1].maleWeightMax", equalTo(16.0F))
                .body("data[1].femaleWeightMin", equalTo(11.0F))
                .body("data[1].femaleWeightMax", equalTo(15.0F))
                .body("data[1].hypoallergenic", equalTo(false))
                .body("data[1].size", equalTo("Médio"));
    }

    @Test
    public void given_noDogBreedsInDatabase_when_getAllDogBreeds_then_returnsNoContent() {
        DogBreed dogBreed = new DogBreed();
        dogBreed.setId(1L);
        dogBreed.setName("Yukina");
        dogBreed.setDescription("Chorando pérolas");
        dogBreed.setLifeExpectancyMin(5);
        dogBreed.setLifeExpectancyMax(10);
        dogBreed.setMaleWeightMin(8.0D);
        dogBreed.setMaleWeightMax(12.0D);
        dogBreed.setFemaleWeightMin(7.0D);
        dogBreed.setFemaleWeightMax(11.0D);
        dogBreed.setHypoallergenic(true);
        dogBreed.setSize("Médio");

        DogBreed savedDogBreed = dogBreedRepository.save(dogBreed);

        given()
                .pathParams("id", savedDogBreed.getId())
                .when()
                .delete("/breeds/{id}")
                .then()
                .statusCode(204);

        boolean exists = dogBreedRepository.existsById(savedDogBreed.getId());
        assertEquals(false, exists, "A raça não foi excluida");
    }


    @Test
    public void given_valid_dogBreedCreateDTO_when_createDogBreed_then_returns_createdDogBreed() {
        DogBreedCreateDTO dogBreedCreateDTO = new DogBreedCreateDTO();
        dogBreedCreateDTO.setName("Nezuko");
        dogBreedCreateDTO.setDescription("Resistência ao sol");
        dogBreedCreateDTO.setLifeExpectancyMin(12);
        dogBreedCreateDTO.setLifeExpectancyMax(15);
        dogBreedCreateDTO.setMaleWeightMin(15D);
        dogBreedCreateDTO.setMaleWeightMax(20D);
        dogBreedCreateDTO.setFemaleWeightMin(14D);
        dogBreedCreateDTO.setFemaleWeightMax(18D);
        dogBreedCreateDTO.setHypoallergenic(true);
        dogBreedCreateDTO.setSize("Médio");

        Long createdDogBreedId =
        given()
                .contentType("application/json")
                .body(dogBreedCreateDTO)
                .when()
                .post("/breeds")
                .then()
                .statusCode(201)
                .body("name", equalTo(dogBreedCreateDTO.getName()))
                .body("description", equalTo(dogBreedCreateDTO.getDescription()))
                .body("lifeExpectancyMin", equalTo(dogBreedCreateDTO.getLifeExpectancyMin()))
                .body("lifeExpectancyMax", equalTo(dogBreedCreateDTO.getLifeExpectancyMax()))
                .body("maleWeightMin", equalTo(dogBreedCreateDTO.getMaleWeightMin().floatValue()))
                .body("maleWeightMax", equalTo(dogBreedCreateDTO.getMaleWeightMax().floatValue()))
                .body("femaleWeightMin", equalTo(dogBreedCreateDTO.getFemaleWeightMin().floatValue()))
                .body("femaleWeightMax", equalTo(dogBreedCreateDTO.getFemaleWeightMax().floatValue()))
                .body("hypoallergenic", equalTo(dogBreedCreateDTO.getHypoallergenic()))
                .body("size", equalTo(dogBreedCreateDTO.getSize()))
                .extract()
                .jsonPath()
                .getLong("id");

        Optional<DogBreed> optionalDogBreed = dogBreedRepository.findById(createdDogBreedId);
        assertTrue(optionalDogBreed.isPresent(), "A raça não foi salva no banco");
    }

    @Test
    public void given_invalid_DogBreedCreateDTO_when_createDogBreed_then_returns_BadRequest() {
        DogBreedCreateDTO dogBreedCreateDTO = new DogBreedCreateDTO();
        dogBreedCreateDTO.setName("Nezuko");
        dogBreedCreateDTO.setDescription("");
        dogBreedCreateDTO.setLifeExpectancyMin(-1);
        dogBreedCreateDTO.setLifeExpectancyMax(null);
        dogBreedCreateDTO.setMaleWeightMin(-5D);
        dogBreedCreateDTO.setMaleWeightMax(10D);
        dogBreedCreateDTO.setFemaleWeightMin(null);
        dogBreedCreateDTO.setFemaleWeightMax(-2D);
        dogBreedCreateDTO.setHypoallergenic(null);
        dogBreedCreateDTO.setSize("");

                given()
                        .contentType("application/json")
                        .body(dogBreedCreateDTO)
                        .when()
                        .post("/breeds")
                        .then()
                        .statusCode(400);

        List<DogBreed> dogBreeds = dogBreedRepository.findAll();
        boolean existsBreed = dogBreeds.stream()
                .anyMatch(breed -> breed.getId() > 0);

        assertFalse(existsBreed, "Nenhuma raça invalida foi salva");
    }

    @Test
    public void given_valid_dogBreedId_when_deleteDogBreed_then_returns_NoContent() {
        DogBreed dogBreed = new DogBreed();
        dogBreed.setId(1L);
        dogBreed.setName("Inosuke");
        dogBreed.setDescription("Cabeça de javali");
        dogBreed.setLifeExpectancyMin(5);
        dogBreed.setLifeExpectancyMax(10);
        dogBreed.setMaleWeightMin(8.0D);
        dogBreed.setMaleWeightMax(12.0D);
        dogBreed.setFemaleWeightMin(7.0D);
        dogBreed.setFemaleWeightMax(11.0D);
        dogBreed.setHypoallergenic(true);
        dogBreed.setSize("Médio");

        dogBreedRepository.save(dogBreed);

        given()
                .pathParams("id", dogBreed.getId())
                .when()
                .delete("/breeds/{id}")
                .then()
                .statusCode(204);

        Optional<DogBreed> optionalDogBreed = dogBreedRepository.findById(dogBreed.getId());
        assertTrue(optionalDogBreed.isEmpty(), "A raça ainda existe no banco de dados.");
    }

    @Test
    public void given_invalid_dogBreedId_when_deleteDogBreed_then_returns_notFound() {
        DogBreed dogBreed = new DogBreed();
        dogBreed.setId(1L);
        dogBreed.setName("Nesuko");
        dogBreed.setDescription("Dormindo na caixa");
        dogBreed.setLifeExpectancyMin(10);
        dogBreed.setLifeExpectancyMax(15);
        dogBreed.setMaleWeightMin(5.0D);
        dogBreed.setMaleWeightMax(10.0D);
        dogBreed.setFemaleWeightMin(4.0D);
        dogBreed.setFemaleWeightMax(9.0D);
        dogBreed.setHypoallergenic(false);
        dogBreed.setSize("Médio");

        dogBreedRepository.save(dogBreed);

        given()
                .pathParam("id", 999L)
                .when()
                .delete("/breeds/{id}")
                .then()
                .statusCode(404);

        DogBreed existingBreed = dogBreedRepository.findById(1L).orElse(null);
        assertNotNull(existingBreed, "A raça de cachorro deveria existir no banco de dados.");
        assertEquals("Nesuko", existingBreed.getName());
        assertEquals("Dormindo na caixa", existingBreed.getDescription());
    }

    @Test
    public void given_valid_dogBreedId_and_valid_data_when_updateDogBreed_then_returns_updateDogBreed() {
        DogBreed dogBreed = new DogBreed();
        dogBreed.setId(1L);
        dogBreed.setName("Nesuko");
        dogBreed.setDescription("Dormindo na caixa");
        dogBreed.setLifeExpectancyMin(10);
        dogBreed.setLifeExpectancyMax(15);
        dogBreed.setMaleWeightMin(5.0D);
        dogBreed.setMaleWeightMax(10.0D);
        dogBreed.setFemaleWeightMin(4.0D);
        dogBreed.setFemaleWeightMax(9.0D);
        dogBreed.setHypoallergenic(false);
        dogBreed.setSize("Médio");

        dogBreedRepository.save(dogBreed);

        DogBreedUpdateDTO updateDTO = new DogBreedUpdateDTO();
        updateDTO.setDescription("Acordada e lutando");
        updateDTO.setLifeExpectancyMin(12);
        updateDTO.setLifeExpectancyMax(16);
        updateDTO.setMaleWeightMin(6.0D);
        updateDTO.setMaleWeightMax(11.0D);
        updateDTO.setFemaleWeightMin(5.0D);
        updateDTO.setFemaleWeightMax(10.0D);
        updateDTO.setHypoallergenic(true);
        updateDTO.setSize("Grande");

        given()
                .pathParam("id", dogBreed.getId())
                .contentType("application/json")
                .body(updateDTO)
                .when()
                .put("/breeds/{id}")
                .then()
                .statusCode(200);

        Optional<DogBreed> optionalDogBreed = dogBreedRepository.findById(dogBreed.getId());
        assertTrue(optionalDogBreed.isPresent(), "A raça não foi encontrada.");

        DogBreed updatedDogBreed = optionalDogBreed.get();
        assertEquals("Acordada e lutando", updatedDogBreed.getDescription());
        assertEquals(12, updatedDogBreed.getLifeExpectancyMin());
        assertEquals(16, updatedDogBreed.getLifeExpectancyMax());
        assertEquals(6.0D, updatedDogBreed.getMaleWeightMin());
        assertEquals(11.0D, updatedDogBreed.getMaleWeightMax());
        assertEquals(5.0D, updatedDogBreed.getFemaleWeightMin());
        assertEquals(10.0D, updatedDogBreed.getFemaleWeightMax());
        assertEquals(true, updatedDogBreed.getHypoallergenic());
        assertEquals("Grande", updatedDogBreed.getSize());
    }

    @Test
    public void given_invalid_Id_when_updateDogBreed_then_returns_BadRequest() {
        DogBreed dogBreed = new DogBreed();
        dogBreed.setId(1L);
        dogBreed.setName("Nesuko");
        dogBreed.setDescription("Dormindo na caixa");
        dogBreed.setLifeExpectancyMin(10);
        dogBreed.setLifeExpectancyMax(15);
        dogBreed.setMaleWeightMin(5.0D);
        dogBreed.setMaleWeightMax(10.0D);
        dogBreed.setFemaleWeightMin(4.0D);
        dogBreed.setFemaleWeightMax(9.0D);
        dogBreed.setHypoallergenic(false);
        dogBreed.setSize("Médio");

        dogBreedRepository.save(dogBreed);

        DogBreedUpdateDTO updateDTO = new DogBreedUpdateDTO();
        updateDTO.setDescription("Acordada e lutando");
        updateDTO.setLifeExpectancyMin(12);
        updateDTO.setLifeExpectancyMax(16);
        updateDTO.setMaleWeightMin(6.0D);
        updateDTO.setMaleWeightMax(11.0D);
        updateDTO.setFemaleWeightMin(5.0D);
        updateDTO.setFemaleWeightMax(10.0D);
        updateDTO.setHypoallergenic(true);
        updateDTO.setSize("Grande");

        given()
                .pathParam("id", "abc")
                .contentType("application/json")
                .body(updateDTO)
                .when()
                .put("/breeds/{id}")
                .then()
                .statusCode(400);

        DogBreed existingBreed = dogBreedRepository.findById(1l).orElse(null);
        assertNotNull(existingBreed);
        assertEquals("Dormindo na caixa", existingBreed.getDescription());
        assertEquals(10, existingBreed.getLifeExpectancyMin());
        assertEquals(15, existingBreed.getLifeExpectancyMax());
        assertEquals(5.0D, existingBreed.getMaleWeightMin());
        assertEquals(10.0D, existingBreed.getMaleWeightMax());
        assertEquals(4.0D, existingBreed.getFemaleWeightMin());
        assertEquals(9.0D, existingBreed.getFemaleWeightMax());
        assertFalse(existingBreed.getHypoallergenic());
        assertEquals("Médio", existingBreed.getSize());
    }

    @Test
    public void given_non_existent_dogBreedId_when_updateDogBreed_then_returns_NotFound() {
        DogBreed dogBreed = new DogBreed();
        dogBreed.setId(1L);
        dogBreed.setName("Nesuko");
        dogBreed.setDescription("Dormindo na caixa");
        dogBreed.setLifeExpectancyMin(10);
        dogBreed.setLifeExpectancyMax(15);
        dogBreed.setMaleWeightMin(5.0D);
        dogBreed.setMaleWeightMax(10.0D);
        dogBreed.setFemaleWeightMin(4.0D);
        dogBreed.setFemaleWeightMax(9.0D);
        dogBreed.setHypoallergenic(false);
        dogBreed.setSize("Médio");

        dogBreedRepository.save(dogBreed);

        DogBreedUpdateDTO updateDTO = new DogBreedUpdateDTO();
        updateDTO.setDescription("Acordada e lutando");
        updateDTO.setLifeExpectancyMin(12);
        updateDTO.setLifeExpectancyMax(16);
        updateDTO.setMaleWeightMin(6.0D);
        updateDTO.setMaleWeightMax(11.0D);
        updateDTO.setFemaleWeightMin(5.0D);
        updateDTO.setFemaleWeightMax(10.0D);
        updateDTO.setHypoallergenic(true);
        updateDTO.setSize("Grande");

        given()
                .pathParam("id", 999L)
                .contentType("application/json")
                .body(updateDTO)
                .when()
                .put("/breeds/{id}")
                .then()
                .statusCode(404);

        DogBreed existingBreed = dogBreedRepository.findById(1L).orElse(null);
        assertNotNull(existingBreed);
        assertEquals("Dormindo na caixa", existingBreed.getDescription());
        assertEquals(10, existingBreed.getLifeExpectancyMin());
        assertEquals(15, existingBreed.getLifeExpectancyMax());
        assertEquals(5.0D, existingBreed.getMaleWeightMin());
        assertEquals(10.0D, existingBreed.getMaleWeightMax());
        assertEquals(4.0D, existingBreed.getFemaleWeightMin());
        assertEquals(9.0D, existingBreed.getFemaleWeightMax());
        assertFalse(existingBreed.getHypoallergenic());
        assertEquals("Médio", existingBreed.getSize());
    }

    @Test
    public void given_valid_data_from_external_api_when_importBreeds_then_imports_and_updates_correctly() throws Exception {
        String payload1 = loadPayload("src/test/resources/payloads/payload1.json");
        String payload2 = loadPayload("src/test/resources/payloads/payload2.json");


        wireMockServer.stubFor(get(urlEqualTo("/breeds?page[number]=1"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(payload1)));

        wireMockServer.stubFor(get(urlEqualTo("/breeds?page[number]=2"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(payload2)));

        wireMockServer.stubFor(get(urlEqualTo("/breeds?page[number]=3"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"breeds\": []}")));

        given()
                .contentType("application/json")
                .when()
                .put("/breeds/import")
                .then()
                .statusCode(200)
                .body(containsString("Importação concluída. 9 raças importadas, 2 raças atualizadas."));

        List<DogBreed> allBreeds = dogBreedRepository.findAll();
        assertEquals(9, allBreeds.size());
    }

    @Test
    public void given_error_from_external_api_when_importBreeds_then_returns_bad_request() throws Exception {
        wireMockServer.stubFor(get(urlEqualTo("/breeds?page[number]=1"))
                .willReturn(aResponse()
                        .withStatus(400)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"error\": \"Erro durante a importação\"}")));

        given()
                .contentType("application/json")
                .when()
                .put("/breeds/import")
                .then()
                .statusCode(400)
                .body(containsString("Erro durante a importação"));

    }
}

