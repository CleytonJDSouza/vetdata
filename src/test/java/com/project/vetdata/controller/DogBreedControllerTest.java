package com.project.vetdata.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.vetdata.dto.*;
import com.project.vetdata.model.DogBreed;
import com.project.vetdata.service.DogBreedExternalService;
import com.project.vetdata.service.DogBreedService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(DogBreedController.class)
public class DogBreedControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DogBreedService dogBreedService;

    @MockBean
    private DogBreedExternalService dogBreedExternalService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void shouldCreateDogBreed() throws Exception {
       DogBreedCreateDTO dogBreedCreateDTO = new DogBreedCreateDTO("Bulldog", "Amigavel e Corajoso", 8, 10, 20.0,
               25.0, 18.0, 23.0, false,"Médio");
       DogBreed dogBreed = new DogBreed(null, null, "Bulldog", "Amigavel e Corajoso", 8, 10, 20.0,
               25.0, 18.0, 23.0, false,"Médio");

        when(dogBreedService.createDogBreed(any(DogBreedCreateDTO.class))).thenReturn(dogBreed);

        mockMvc.perform(MockMvcRequestBuilders.post("/breeds")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dogBreedCreateDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(dogBreed.getName()))
                .andExpect(jsonPath("$.description").value(dogBreed.getDescription()))
                .andExpect(jsonPath("$.lifeExpectancyMin").value(dogBreed.getLifeExpectancyMin()))
                .andExpect(jsonPath("$.lifeExpectancyMax").value(dogBreed.getLifeExpectancyMax()))
                .andExpect(jsonPath("$.maleWeightMin").value(dogBreed.getMaleWeightMin()))
                .andExpect(jsonPath("$.maleWeightMax").value(dogBreed.getMaleWeightMax()))
                .andExpect(jsonPath("$.femaleWeightMin").value(dogBreed.getFemaleWeightMin()))
                .andExpect(jsonPath("$.femaleWeightMax").value(dogBreed.getFemaleWeightMax()))
                .andExpect(jsonPath("$.hypoallergenic").value(dogBreed.getHypoallergenic()))
                .andExpect(jsonPath("$.size").value(dogBreed.getSize()));
    }

    @Test
    public void shouldReturnError() throws Exception {
        DogBreedCreateDTO dogBreedCreateDTO = new DogBreedCreateDTO();
        dogBreedCreateDTO.setName(null);
        dogBreedCreateDTO.setDescription(null);
        dogBreedCreateDTO.setLifeExpectancyMin(-8);
        dogBreedCreateDTO.setLifeExpectancyMax(-10);
        dogBreedCreateDTO.setMaleWeightMin(-20.0);
        dogBreedCreateDTO.setMaleWeightMax(-25.0);
        dogBreedCreateDTO.setFemaleWeightMin(-18.0);
        dogBreedCreateDTO.setFemaleWeightMax(-23.0);
        dogBreedCreateDTO.setHypoallergenic(false);
        dogBreedCreateDTO.setSize(null);

        mockMvc.perform(MockMvcRequestBuilders.post("/breeds")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dogBreedCreateDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").value("Campo Obrigatório"))
                .andExpect(jsonPath("$.lifeExpectancyMin").value("Campo deve ter valor maior que: 0"))
                .andExpect(jsonPath("$.lifeExpectancyMax").value("Campo deve ter valor maior que: 0"))
                .andExpect(jsonPath("$.maleWeightMin").value("Campo deve ter valor maior que: 0"))
                .andExpect(jsonPath("$.maleWeightMax").value("Campo deve ter valor maior que: 0"))
                .andExpect(jsonPath("$.femaleWeightMin").value("Campo deve ter valor maior que: 0"))
                .andExpect(jsonPath("$.femaleWeightMax").value("Campo deve ter valor maior que: 0"))
                .andExpect(jsonPath("$.size").value("Campo Obrigatório"));
    }

    @Test
    public void shouldReturnBreedById() throws Exception {
        DogBreed dogBreed = new DogBreed(12345L, null ,"Bulldog", "Amigavel e Corajoso", 8, 10,
                20.0, 25.0, 18.0, 23.0, false, "Médio");

        when(dogBreedService.getDogBreedId(12345L)).thenReturn(Optional.of(dogBreed));

        mockMvc.perform(MockMvcRequestBuilders.get("/breeds/12345")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(12345))
                .andExpect(jsonPath("$.name").value("Bulldog"))
                .andExpect(jsonPath("$.description").value("Amigavel e Corajoso"))
                .andExpect(jsonPath("$.lifeExpectancyMin").value(8))
                .andExpect(jsonPath("$.lifeExpectancyMax").value(10))
                .andExpect(jsonPath("$.maleWeightMin").value(20.0))
                .andExpect(jsonPath("$.maleWeightMax").value(25.0))
                .andExpect(jsonPath("$.femaleWeightMin").value(18.0))
                .andExpect(jsonPath("$.femaleWeightMax").value(23.0))
                .andExpect(jsonPath("$.hypoallergenic").value(false))
                .andExpect(jsonPath("$.size").value("Médio"));

    }

    @Test
    public void shouldReturnNotFound() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/breeds/56789")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
    @Test
    public void shouldDeleteBreedId() throws Exception {
        DogBreed dogBreed = new DogBreed(12345L, null,"Bulldog", "Amigavel e Corajoso", 8, 10,
                20.0, 25.0, 18.0, 23.0, false, "Médio");

        when(dogBreedService.getDogBreedId(12345L)).thenReturn(Optional.of(dogBreed));

        mockMvc.perform(MockMvcRequestBuilders.delete("/breeds/12345"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void shouldDeleteBreedNotFound() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.delete("/breeds/56789"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldUpdateDogBreed() throws Exception {
        DogBreed dogBreed = new DogBreed(12345L, null,"Bulldog", "Amigável e Corajoso", 8, 10,
                20.0, 25.0, 18.0, 23.0, false, "Médio");

        DogBreedUpdateDTO dogBreedUpdateDTO = new DogBreedUpdateDTO();
        dogBreedUpdateDTO.setDescription("Leal e Protetor");
        dogBreedUpdateDTO.setLifeExpectancyMin(9);
        dogBreedUpdateDTO.setLifeExpectancyMax(12);
        dogBreedUpdateDTO.setMaleWeightMin(22.0);
        dogBreedUpdateDTO.setMaleWeightMax(28.0);
        dogBreedUpdateDTO.setFemaleWeightMin(20.0);
        dogBreedUpdateDTO.setFemaleWeightMax(26.0);
        dogBreedUpdateDTO.setHypoallergenic(true);
        dogBreedUpdateDTO.setSize("Pequeno");

        DogBreed updatedDogBreed = new DogBreed(12345L, null,"Bulldog", "Leal e Protetor", 9, 12,
                22.0, 28.0, 20.0, 26.0, true, "Pequeno");

        when(dogBreedService.updateDogBreed(anyLong(), any(DogBreedUpdateDTO.class))).thenReturn(updatedDogBreed);

        mockMvc.perform(MockMvcRequestBuilders.put("/breeds/12345")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dogBreedUpdateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(12345))
                .andExpect(jsonPath("$.name").value("Bulldog"))
                .andExpect(jsonPath("$.description").value("Leal e Protetor"))
                .andExpect(jsonPath("$.lifeExpectancyMin").value(9))
                .andExpect(jsonPath("$.lifeExpectancyMax").value(12))
                .andExpect(jsonPath("$.maleWeightMin").value(22.0))
                .andExpect(jsonPath("$.maleWeightMax").value(28.0))
                .andExpect(jsonPath("$.femaleWeightMin").value(20.0))
                .andExpect(jsonPath("$.femaleWeightMax").value(26.0))
                .andExpect(jsonPath("$.hypoallergenic").value(true))
                .andExpect(jsonPath("$.size").value("Pequeno"));
    }

    @Test
    public void shouldReturnPageBreeds() throws Exception {
        List<DogBreed> breeds = List.of(
                new DogBreed(12345L, null,"Golden Retriever", "Amigável e inteligente", 10, 12, 29.0,
                        34.0, 25.0, 32.0, false, "Grande"),
                new DogBreed(67890L, null,"Labrador Retriever", "Raça popular conhecida por sua natureza amigável", 10,
                        12, 29.0, 36.0, 25.0, 32.0, false, "Grande")
        );
        Pageable paging = PageRequest.of(0, 2, Sort.by("id"));
        Page<DogBreed> pageBreeds = new PageImpl<>(breeds, paging, breeds.size());

        when(dogBreedService.getAllDogBreeds(Mockito.any(Pageable.class))).thenReturn(pageBreeds);

            mockMvc.perform(MockMvcRequestBuilders.get("/breeds?page=0&qtdRecordsPage=2&sortBy=id")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.total").value(pageBreeds.getTotalElements()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.page").value(pageBreeds.getNumber()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].id").value(12345))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[1].id").value(67890));
    }

    @Test
    public void shouldImportBreeds() throws Exception {
        DogBreedExternalDTO dogBreedExternalDTO = new DogBreedExternalDTO();
        dogBreedExternalDTO.setIdExternalApi("1");
        dogBreedExternalDTO.setAttributeDTO(new AttributesDTO());
        dogBreedExternalDTO.getAttributeDTO().setName("Golden Retriever");
        dogBreedExternalDTO.getAttributeDTO().setDescription("Amigavel e inteligente");
        dogBreedExternalDTO.getAttributeDTO().setLife(new LifeDTO());
        dogBreedExternalDTO.getAttributeDTO().getLife().setMin(10);
        dogBreedExternalDTO.getAttributeDTO().getLife().setMax(12);
        dogBreedExternalDTO.getAttributeDTO().setMaleWeightDTO(new MaleWeightDTO());
        dogBreedExternalDTO.getAttributeDTO().getMaleWeightDTO().setMin(29.0);
        dogBreedExternalDTO.getAttributeDTO().getMaleWeightDTO().setMax(34.0);
        dogBreedExternalDTO.getAttributeDTO().setFemaleWeightDTO(new FemaleWeightDTO());
        dogBreedExternalDTO.getAttributeDTO().getFemaleWeightDTO().setMin(25.0);
        dogBreedExternalDTO.getAttributeDTO().getFemaleWeightDTO().setMax(32.0);
        dogBreedExternalDTO.getAttributeDTO().setHypoallergenic(false);

        List<DogBreedExternalDTO> dogBreedExternalDTOList = new ArrayList<>();
        dogBreedExternalDTOList.add(dogBreedExternalDTO);

        when(dogBreedExternalService.getBreedsByPage(anyInt()))
                .thenReturn(dogBreedExternalDTOList)
                .thenReturn(Collections.emptyList());

        when(dogBreedService.findByExternalApi(dogBreedExternalDTO.getIdExternalApi())).thenReturn(Collections.emptyList());

        doNothing().when(dogBreedExternalService).saveFromExternalAPI(any());

        mockMvc.perform(MockMvcRequestBuilders.put("/breeds/import"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.TEXT_PLAIN_VALUE + ";charset=UTF-8"))
                .andExpect(content().string("Importação concluída. 1 raças importadas, 0 raças atualizadas."));

        verify(dogBreedExternalService, times(1)).saveFromExternalAPI(eq(dogBreedExternalDTOList));
    }

    @Test
    public void shouldUpgradeExistingBreed() throws Exception {
        DogBreed existingBreed = new DogBreed();
        existingBreed.setId(1L);
        existingBreed.setName("Golden Retriever");
        existingBreed.setDescription("Amigável e inteligente");
        existingBreed.setLifeExpectancyMin(10);
        existingBreed.setLifeExpectancyMax(12);
        existingBreed.setMaleWeightMin(29.0);
        existingBreed.setMaleWeightMax(34.0);
        existingBreed.setFemaleWeightMin(25.0);
        existingBreed.setFemaleWeightMax(32.0);
        existingBreed.setHypoallergenic(false);


        DogBreedExternalDTO updatedBreedDTO = new DogBreedExternalDTO();
        updatedBreedDTO.setIdExternalApi("1");
        updatedBreedDTO.setAttributeDTO(new AttributesDTO());
        updatedBreedDTO.getAttributeDTO().setName("Golden Retriever");
        updatedBreedDTO.getAttributeDTO().setDescription("Amigável e inteligente e esperto");
        updatedBreedDTO.getAttributeDTO().setLife(new LifeDTO());
        updatedBreedDTO.getAttributeDTO().getLife().setMin(11);
        updatedBreedDTO.getAttributeDTO().getLife().setMax(13);
        updatedBreedDTO.getAttributeDTO().setMaleWeightDTO(new MaleWeightDTO());
        updatedBreedDTO.getAttributeDTO().getMaleWeightDTO().setMin(30.0);
        updatedBreedDTO.getAttributeDTO().getMaleWeightDTO().setMax(35.0);
        updatedBreedDTO.getAttributeDTO().setFemaleWeightDTO(new FemaleWeightDTO());
        updatedBreedDTO.getAttributeDTO().getFemaleWeightDTO().setMin(26.0);
        updatedBreedDTO.getAttributeDTO().getFemaleWeightDTO().setMax(33.0);
        updatedBreedDTO.getAttributeDTO().setHypoallergenic(true);

        when(dogBreedService.findByExternalApi("1"))
                .thenReturn(Collections.singletonList(existingBreed))
                .thenReturn(Collections.singletonList(existingBreed));

        when(dogBreedExternalService.getBreedsByPage(anyInt()))
                .thenReturn(Collections.singletonList(updatedBreedDTO))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders.put("/breeds/import"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.TEXT_PLAIN_VALUE + ";charset=UTF-8"))
                .andExpect(content().string("Importação concluída. 0 raças importadas, 1 raças atualizadas."));

        DogBreed updatedBreedAfterUpdate = dogBreedService.findByExternalApi("1").get(0);
        assertEquals("Amigável e inteligente e esperto", updatedBreedAfterUpdate.getDescription());
        assertEquals(11, updatedBreedAfterUpdate.getLifeExpectancyMin());
        assertEquals(13, updatedBreedAfterUpdate.getLifeExpectancyMax());
        assertEquals(30.0, updatedBreedAfterUpdate.getMaleWeightMin());
        assertEquals(35.0, updatedBreedAfterUpdate.getMaleWeightMax());
        assertEquals(26.0, updatedBreedAfterUpdate.getFemaleWeightMin());
        assertEquals(33.0, updatedBreedAfterUpdate.getFemaleWeightMax());
        assertTrue(updatedBreedAfterUpdate.getHypoallergenic());
    }
}