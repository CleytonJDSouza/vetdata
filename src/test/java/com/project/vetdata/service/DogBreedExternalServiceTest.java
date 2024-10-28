package com.project.vetdata.service;

import com.project.vetdata.dto.*;
import com.project.vetdata.model.DogBreed;
import com.project.vetdata.repository.DogBreedRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DogBreedExternalServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private DogBreedRepository dogBreedRepository;

    @InjectMocks
    private DogBreedExternalService dogBreedExternalService;

    private final String apiUrl = "http://external-api.com/breeds";

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(dogBreedExternalService, "apiUrl", apiUrl);
    }

    @Test
    public void given_externalDTOList_when_saveFromExternalAPI_is_called_then_dogBreed_is_saved() {
        List<DogBreedExternalDTO> externalDTOS = getFakeDogBreedExternalDTOList();
        DogBreed dogBreed = getFakeDogBreed();

        when(dogBreedRepository.save(any(DogBreed.class))).thenReturn(dogBreed);
        dogBreedExternalService.saveFromExternalAPI(externalDTOS);
        verify(dogBreedRepository, times(externalDTOS.size())).save(any(DogBreed.class));
    }

    @Test
    public void given_valid_pageNumber_when_getBreedsByPage_is_called_then_breeds_are_returned() {
        int pageNumber = 1;
        String url = apiUrl + "?page[number]=" + pageNumber;
        DogBreedResponseDTO responseDTO = new DogBreedResponseDTO();
        responseDTO.setData(getFakeDogBreedExternalDTOList());

        when(restTemplate.getForObject(url, DogBreedResponseDTO.class)).thenReturn(responseDTO);
        List<DogBreedExternalDTO> result = dogBreedExternalService.getBreedsByPage(pageNumber);

        assertNotNull(result, "A lista de raças não deve ser nula");
        assertEquals(2, result.size(), "A lista de raças deve conter 2 itens");
        assertEquals("Golden Retriever", result.get(0).getAttributeDTO().getName(), "A primeira raça deve ser Golden Retriever");
        assertEquals("Labrador", result.get(1).getAttributeDTO().getName(), "A segunda raça deve ser Labrador");
        verify(restTemplate, times(1)).getForObject(url, DogBreedResponseDTO.class);
    }

    @Test
    public void given_invalid_pageNumber_when_getBreedsByPage_is_called_then_empty_list_is_returned() {
        int pageNumber = 999;
        String url = apiUrl + "?page[number]=" + pageNumber;

        when(restTemplate.getForObject(url, DogBreedResponseDTO.class)).thenReturn(null);
        List<DogBreedExternalDTO> result = dogBreedExternalService.getBreedsByPage(pageNumber);

        assertNotNull(result, "A lista de raças não deve ser nula");
        assertEquals(0, result.size(), "A lista deve ser vazia");
        verify(restTemplate, times(1)).getForObject(url, DogBreedResponseDTO.class);
    }

    @Test
    public void given_empty_externalDTOList_when_saveFromExternalAPI_is_called_then_no_dogBreed_is_saved() {
        List<DogBreedExternalDTO> externalDTOS = new ArrayList<>();
        dogBreedExternalService.saveFromExternalAPI(externalDTOS);
        verify(dogBreedRepository, never()).save(any(DogBreed.class));
    }

    @Test
    public void given_existingDogBreed_when_convertToUpdateDTO_is_called_then_DTO_is_returned() {
        DogBreed existingBreed = getFakeDogBreed();
        DogBreedUpdateDTO updateDTO = dogBreedExternalService.convertToUpdateDTO(existingBreed);

        assertNotNull(updateDTO, "O DTO de atualização não pode ser nulo");
        assertEquals(existingBreed.getDescription(), updateDTO.getDescription(), "A descrição deve ser a mesma");
        assertEquals(existingBreed.getLifeExpectancyMin(), updateDTO.getLifeExpectancyMin(), "A expectativa de vida mínima deve ser a mesma");
        assertEquals(existingBreed.getLifeExpectancyMax(), updateDTO.getLifeExpectancyMax(), "A expectativa de vida máxima deve ser a mesma");
        assertEquals(existingBreed.getMaleWeightMin(), updateDTO.getMaleWeightMin(), "O peso mínimo do macho deve ser o mesmo");
        assertEquals(existingBreed.getMaleWeightMax(), updateDTO.getMaleWeightMax(), "O peso máximo do macho deve ser o mesmo");
        assertEquals(existingBreed.getFemaleWeightMin(), updateDTO.getFemaleWeightMin(), "O peso mínimo da fêmea deve ser o mesmo");
        assertEquals(existingBreed.getFemaleWeightMax(), updateDTO.getFemaleWeightMax(), "O peso máximo da fêmea deve ser o mesmo");
        assertEquals(existingBreed.getHypoallergenic(), updateDTO.getHypoallergenic(), "A informação de hipoalergenicidade deve ser a mesma");
        assertEquals(existingBreed.getSize(), updateDTO.getSize(), "O tamanho deve ser o mesmo (mesmo que esteja nulo)");
    }

    @Test
    void given_DogBreedWithNoChanges_when_checkForUpdates_then_returnFalse() {
        DogBreed existingBreed = new DogBreed();
        existingBreed.setDescription("Companheiro");
        existingBreed.setLifeExpectancyMin(10);
        existingBreed.setLifeExpectancyMax(12);
        existingBreed.setMaleWeightMin(25D);
        existingBreed.setMaleWeightMax(30D);
        existingBreed.setFemaleWeightMin(22D);
        existingBreed.setFemaleWeightMax(27D);
        existingBreed.setHypoallergenic(false);

        AttributesDTO newAttributes = new AttributesDTO();
        newAttributes.setDescription("Companheiro");
        newAttributes.setLife(new LifeDTO(10, 12));
        newAttributes.setMaleWeightDTO(new MaleWeightDTO(25D, 30D));
        newAttributes.setFemaleWeightDTO(new FemaleWeightDTO(22D, 27D));
        newAttributes.setHypoallergenic(false);

        DogBreedExternalDTO newBreedDTO = new DogBreedExternalDTO();
        newBreedDTO.setAttributeDTO(newAttributes);

        boolean result = DogBreedExternalService.checkForUpdates(existingBreed, newBreedDTO);

        assertFalse(result);
    }

    @Test
    void given_DogBreedWithChanges_when_checkForUpdates_then_returnTrue() {
        DogBreed existingBreed = new DogBreed();
        existingBreed.setDescription("Companheiro");
        existingBreed.setLifeExpectancyMin(10);
        existingBreed.setLifeExpectancyMax(12);
        existingBreed.setMaleWeightMin(25D);
        existingBreed.setMaleWeightMax(30D);
        existingBreed.setFemaleWeightMin(22D);
        existingBreed.setFemaleWeightMax(27D);
        existingBreed.setHypoallergenic(false);

        AttributesDTO newAttributes = new AttributesDTO();
        newAttributes.setDescription("Companheiro");
        newAttributes.setLife(new LifeDTO(11, 13));
        newAttributes.setMaleWeightDTO(new MaleWeightDTO(26D, 31D));
        newAttributes.setFemaleWeightDTO(new FemaleWeightDTO(23D, 28D));
        newAttributes.setHypoallergenic(true);

        DogBreedExternalDTO newBreedDTO = new DogBreedExternalDTO();
        newBreedDTO.setAttributeDTO(newAttributes);

        boolean result = DogBreedExternalService.checkForUpdates(existingBreed, newBreedDTO);

        assertTrue(result);
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

    public DogBreed getFakeDogBreed() {
        DogBreed dogBreed = new DogBreed();
        dogBreed.setId(1L);
        dogBreed.setIdExternalApi("1");
        dogBreed.setName("Labrador");
        dogBreed.setDescription("Leal e carinhoso");
        dogBreed.setLifeExpectancyMin(12);
        dogBreed.setLifeExpectancyMax(14);
        dogBreed.setMaleWeightMin(30D);
        dogBreed.setMaleWeightMax(36D);
        dogBreed.setFemaleWeightMin(27D);
        dogBreed.setFemaleWeightMax(33D);
        dogBreed.setHypoallergenic(false);
        dogBreed.setSize(null);
        return dogBreed;
    }
}