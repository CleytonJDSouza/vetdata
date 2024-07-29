package com.project.vetdata.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.vetdata.dto.DogBreedUpdateDTO;
import com.project.vetdata.model.DogBreed;
import com.project.vetdata.service.DogBreedService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DogBreedController.class)
public class DogBreedControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DogBreedService dogBreedService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void shouldCreateDogBreed() throws Exception {
        DogBreed dogBreed = new DogBreed(null, "Bulldog", "Amigavel e Corajoso", 8, 10,
                20.0, 25.0, 18.0, 23.0, false, "Médio");

        Mockito.when(dogBreedService.createDogBreed(any(DogBreed.class))).thenReturn(dogBreed);

        mockMvc.perform(MockMvcRequestBuilders.post("/breeds")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dogBreed)))
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
        DogBreed dogBreed = new DogBreed(null, null, null, -8, -10,
                -20.0, -25.0, -18.0, -23.0, false, null);

        mockMvc.perform(MockMvcRequestBuilders.post("/breeds")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dogBreed)))
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
        DogBreed dogBreed = new DogBreed(12345L, "Bulldog", "Amigavel e Corajoso", 8, 10,
                20.0, 25.0, 18.0, 23.0, false, "Médio");

        Mockito.when(dogBreedService.getDogBreedId(12345L)).thenReturn(Optional.of(dogBreed));

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
        DogBreed dogBreed = new DogBreed(12345L, "Bulldog", "Amigavel e Corajoso", 8, 10,
                20.0, 25.0, 18.0, 23.0, false, "Médio");

        Mockito.when(dogBreedService.getDogBreedId(12345L)).thenReturn(Optional.of(dogBreed));

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
        DogBreed dogBreed = new DogBreed(12345L, "Bulldog", "Amigável e Corajoso", 8, 10,
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
        dogBreedUpdateDTO.setSize("Small");

        DogBreed updatedDogBreed = new DogBreed(12345L, "Bulldog", "Leal e Protetor", 9, 12,
                22.0, 28.0, 20.0, 26.0, true, "Pequeno");

        Mockito.when(dogBreedService.updateDogBreed(anyLong(), any(DogBreedUpdateDTO.class))).thenReturn(updatedDogBreed);

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
}