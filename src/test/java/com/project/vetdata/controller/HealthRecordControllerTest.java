package com.project.vetdata.controller;

import com.project.vetdata.dto.HealthRecordCreateDTO;
import com.project.vetdata.enums.DogSize;
import com.project.vetdata.enums.Euthanasia;
import com.project.vetdata.enums.Gender;
import com.project.vetdata.model.DogBreed;
import com.project.vetdata.model.HealthRecord;
import com.project.vetdata.service.HealthRecordService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;


import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HealthRecordController.class)
@ActiveProfiles("test")
public class HealthRecordControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private HealthRecordService healthRecordService;

    @Test
    public void given_valid_healthRecord_when_createHealthRecord_then_returns_createdHealthRecord() throws Exception {
        HealthRecordCreateDTO dto = new HealthRecordCreateDTO(LocalDate.of(2024, 4, 9), 3.5, "C123", "Preto", false,
                Euthanasia.NO, Gender.MALE, "Torresmo", DogSize.SMALL, "Cleyton", 12.0, 1L
        );

        HealthRecord savedRecord = new HealthRecord(10L, dto.getAdmission(), dto.getAge(), dto.getCodPatient(), dto.getColor(), dto.getDeath(), dto.getEuthanasia(),
                dto.getGender(), dto.getPatient(), dto.getSize(), dto.getTutor(), dto.getWeight(), new DogBreed(1L, "123", "Labrador",
                "Amigavel e Corajoso", 8, 10, 20.0, 25.0, 18.0, 23.0,
                false,"Médio"));

        when(healthRecordService.createHealthRecord(any(HealthRecordCreateDTO.class))).thenReturn(savedRecord);

        mockMvc.perform(post("/health-records")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(savedRecord.getId()))
                .andExpect(jsonPath("$.patient").value(savedRecord.getPatient()))
                .andExpect(jsonPath("$.tutor").value(savedRecord.getTutor()))
                .andExpect(jsonPath("$.breed.name").value("Labrador"));
    }

    @Test
    public void given_invalid_healthRecord_when_createHealthRecord_then_returnsBadRequest() throws Exception {
        HealthRecordCreateDTO dto = new HealthRecordCreateDTO();
        dto.setAdmission(null);
        dto.setAge(-1.0);
        dto.setCodPatient("");
        dto.setColor("");
        dto.setDeath(null);
        dto.setEuthanasia(null);
        dto.setGender(null);
        dto.setPatient("");
        dto.setSize(null);
        dto.setTutor("");
        dto.setWeight(-5.0);
        dto.setBreedId(null);

        mockMvc.perform(post("/health-records")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.admission").value("Campo Obrigatório"))
                .andExpect(jsonPath("$.euthanasia").value("Campo Obrigatório"))
                .andExpect(jsonPath("$.gender").value("Campo Obrigatório"))
                .andExpect(jsonPath("$.patient").value("Campo Obrigatório"))
                .andExpect(jsonPath("$.size").value("Campo Obrigatório"))
                .andExpect(jsonPath("$.tutor").value("Campo Obrigatório"))
                .andExpect(jsonPath("$.weight").value("Campo deve ter valor maior que 0"))
                .andExpect(jsonPath("$.breedId").value("Campo Obrigatório"));
    }
}
