package com.project.vetdata.service;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.project.vetdata.dto.*;
import com.project.vetdata.exception.ExternalAPIException;
import com.project.vetdata.model.DogBreed;
import com.project.vetdata.repository.DogBreedRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class DogBreedExternalServiceIT {

    private static final int WIREMOCK_PORT = 8082;

    @Container
    private static final MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8.0.26");

    @Autowired
    private DogBreedRepository dogBreedRepository;

    @Autowired
    private DogBreedExternalService dogBreedExternalService;

    private static WireMockServer wireMockServer;

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
    void connectionEstablished() {
        assertThat(mysqlContainer.isCreated()).isTrue();
        assertThat(mysqlContainer.isRunning()).isTrue();
    }

    @Test
    public void given_ValidDogBreedsFromExternalAPI_when_saveFromExternalAPI_then_BreedsSaved() {
        List<DogBreedExternalDTO> externalDTOS = getFakeDogBreedExternalDTOList();

        dogBreedExternalService.saveFromExternalAPI(externalDTOS);

        List<DogBreed> savedBreeds = dogBreedRepository.findAll();
        assertThat(savedBreeds).isNotEmpty();
        assertThat(savedBreeds.get(0).getName()).isEqualTo("Golden Retriever");
        assertThat(savedBreeds.get(1).getName()).isEqualTo("Labrador");
        assertThat(savedBreeds.get(0).getDescription()).isEqualTo("Amigável e inteligente");
        assertThat(savedBreeds.get(1).getDescription()).isEqualTo("Leal e carinhoso");
    }

    @Test
    public void given_ValidPageRequest_when_getBreedsByPage_then_ReturnsDogBreeds() throws Exception{
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

        List<DogBreedExternalDTO> breeds = dogBreedExternalService.getBreedsByPage(1);

        assertThat(breeds).isNotEmpty();
        assertThat(breeds.size()).isEqualTo(10);
        assertThat(breeds.get(0).getAttributeDTO().getName()).isEqualTo("Tanjiro");
        assertThat(breeds.get(1).getAttributeDTO().getName()).isEqualTo("Ana");
        assertThat(breeds.get(2).getAttributeDTO().getName()).isEqualTo("Batatinha");
        assertThat(breeds.get(3).getAttributeDTO().getName()).isEqualTo("Luciano");
        assertThat(breeds.get(4).getAttributeDTO().getName()).isEqualTo("Zeze");
        assertThat(breeds.get(5).getAttributeDTO().getName()).isEqualTo("Torresmo");
        assertThat(breeds.get(6).getAttributeDTO().getName()).isEqualTo("Goku");
        assertThat(breeds.get(7).getAttributeDTO().getName()).isEqualTo("Naruto");
        assertThat(breeds.get(8).getAttributeDTO().getName()).isEqualTo("Hiei");
        assertThat(breeds.get(9).getAttributeDTO().getName()).isEqualTo("Yusuki");

        List<DogBreedExternalDTO> breeds2 = dogBreedExternalService.getBreedsByPage(2);

        assertThat(breeds2).isNotEmpty();
        assertThat(breeds2.size()).isEqualTo(2);
        assertThat(breeds2.get(0).getAttributeDTO().getName()).isEqualTo("Ynusuke");
        assertThat(breeds2.get(1).getAttributeDTO().getName()).isEqualTo("Nezuko");
    }

    @Test
    public void given_ExternalAPIError_when_getBreedsByPage_then_ThrowsExternalAPIException() {
        wireMockServer.stubFor(get(urlEqualTo("/breeds?page[number]=1"))
                .willReturn(aResponse()
                        .withStatus(503)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"error\": \"Service Unavailable\"}")));

        ExternalAPIException exception = assertThrows(ExternalAPIException.class, () -> {
            dogBreedExternalService.getBreedsByPage(1);
        });

        assertTrue(exception.getMessage().contains("Serviço indisponível, tente novamente mais tarde."));
    }

    @Test
    public void given_EmptyResponse_when_getBreedsByPage_then_ReturnsEmptyList() {
        wireMockServer.stubFor(get(urlEqualTo("/breeds?page[number]=1"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"data\": []}")));

        List<DogBreedExternalDTO> result = dogBreedExternalService.getBreedsByPage(1);

        assertTrue(result.isEmpty(), "A lista deve estar vazia");
    }

    public List<DogBreedExternalDTO> getFakeDogBreedExternalDTOList() {
        List<DogBreedExternalDTO> externalDTOS = new ArrayList<>();

        DogBreedExternalDTO goldenRetriever = new DogBreedExternalDTO();
        goldenRetriever.setIdExternalApi("1");
        goldenRetriever.setAttributeDTO(new AttributesDTO());
        goldenRetriever.getAttributeDTO().setName("Golden Retriever");
        goldenRetriever.getAttributeDTO().setDescription("Amigável e inteligente");
        goldenRetriever.getAttributeDTO().setLife(new LifeDTO());
        goldenRetriever.getAttributeDTO().getLife().setMin(10);
        goldenRetriever.getAttributeDTO().getLife().setMax(12);
        goldenRetriever.getAttributeDTO().setMaleWeightDTO(new MaleWeightDTO());
        goldenRetriever.getAttributeDTO().getMaleWeightDTO().setMin(29D);
        goldenRetriever.getAttributeDTO().getMaleWeightDTO().setMax(34D);
        goldenRetriever.getAttributeDTO().setFemaleWeightDTO(new FemaleWeightDTO());
        goldenRetriever.getAttributeDTO().getFemaleWeightDTO().setMin(25D);
        goldenRetriever.getAttributeDTO().getFemaleWeightDTO().setMax(32D);
        goldenRetriever.getAttributeDTO().setHypoallergenic(false);
        externalDTOS.add(goldenRetriever);

        DogBreedExternalDTO labrador = new DogBreedExternalDTO();
        labrador.setIdExternalApi("2");
        labrador.setAttributeDTO(new AttributesDTO());
        labrador.getAttributeDTO().setName("Labrador");
        labrador.getAttributeDTO().setDescription("Leal e carinhoso");
        labrador.getAttributeDTO().setLife(new LifeDTO());
        labrador.getAttributeDTO().getLife().setMin(12);
        labrador.getAttributeDTO().getLife().setMax(14);
        labrador.getAttributeDTO().setMaleWeightDTO(new MaleWeightDTO());
        labrador.getAttributeDTO().getMaleWeightDTO().setMin(30D);
        labrador.getAttributeDTO().getMaleWeightDTO().setMax(36D);
        labrador.getAttributeDTO().setFemaleWeightDTO(new FemaleWeightDTO());
        labrador.getAttributeDTO().getFemaleWeightDTO().setMin(27D);
        labrador.getAttributeDTO().getFemaleWeightDTO().setMax(33D);
        labrador.getAttributeDTO().setHypoallergenic(false);
        externalDTOS.add(labrador);

        return externalDTOS;
    }
}