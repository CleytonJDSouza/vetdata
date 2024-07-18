package com.project.vetdata.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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

import static org.mockito.ArgumentMatchers.any;
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
}