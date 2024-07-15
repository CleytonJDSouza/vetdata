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
        DogBreed dogBreed = DogBreed.builder()
                .name("Bulldog")
                .description("Friendly and courageous")
                .lifeExpectancyMin(8)
                .lifeExpectancyMax(10)
                .maleWeightMin(20.0)
                .maleWeightMax(25.0)
                .femaleWeightMin(18.0)
                .femaleWeightMax(23.0)
                .hypoallergenic(false)
                .size("Medium")
                .build();

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
                .andExpect(jsonPath("$.hypoallergenic").value(dogBreed.isHypoallergenic()))
                .andExpect(jsonPath("$.size").value(dogBreed.getSize()));
    }
}