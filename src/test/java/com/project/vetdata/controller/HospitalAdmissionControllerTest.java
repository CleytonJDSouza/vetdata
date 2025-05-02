package com.project.vetdata.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.vetdata.dto.HospitalAdmissionCreateDTO;
import com.project.vetdata.dto.HospitalAdmissionResponseDTO;
import com.project.vetdata.model.HospitalAdmission;
import com.project.vetdata.service.HospitalAdmissionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HospitalAdmissionController.class)
@ActiveProfiles("test")
public class HospitalAdmissionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private HospitalAdmissionService hospitalAdmissionService;

    @Test
    public void given_valid_hospitalAdmission_when_createHospitalAdmission_then_return_created() throws Exception {
        HospitalAdmissionCreateDTO dto = new HospitalAdmissionCreateDTO();
        dto.setDate(LocalDate.of(2024, 2, 25));
        dto.setReasonHospitalization("Infecção");
        dto.setDateMedicalDischarge(LocalDate.of(2024, 3, 4));
        dto.setDateReturns(LocalDate.of(2024, 4, 17));
        dto.setMedicalEvolution("Boa evolução");
        dto.setTreatmentNextSteps("Repouso");
        dto.setHealthRecordId(1L);
        dto.setPostOperativeIds(Set.of(1L));
        dto.setDiagnosticIds(Set.of(1L));

        HospitalAdmissionResponseDTO created = new HospitalAdmissionResponseDTO();
        created.setDate(dto.getDate());
        created.setReasonHospitalization(dto.getReasonHospitalization());
        created.setDateMedicalDischarge(dto.getDateMedicalDischarge());
        created.setDateReturns(dto.getDateReturns());
        created.setMedicalEvolution(dto.getMedicalEvolution());
        created.setTreatmentNextSteps(dto.getTreatmentNextSteps());

        when(hospitalAdmissionService.createHospitalAdmission(any(HospitalAdmissionCreateDTO.class))).thenReturn(created);

        mockMvc.perform(post("/hospital-admission")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(created.getId()))
                .andExpect(jsonPath("$.reasonHospitalization").value("Infecção"))
                .andExpect(jsonPath("$.medicalEvolution").value("Boa evolução"))
                .andExpect(jsonPath("$.treatmentNextSteps").value("Repouso"));
    }

    @Test
    public void given_invalid_hospitalAdmission_when_createHospitalAdmission_then_returns_badRequest() throws Exception {
        HospitalAdmissionCreateDTO dto = new HospitalAdmissionCreateDTO();
        dto.setDate(null);
        dto.setReasonHospitalization("");
        dto.setDateMedicalDischarge(null);
        dto.setDateReturns(LocalDate.of(2024, 4, 17));
        dto.setMedicalEvolution("Boa evolução");
        dto.setTreatmentNextSteps("Repouso");
        dto.setHealthRecordId(null);
        dto.setPostOperativeIds(Set.of(1L));
        dto.setDiagnosticIds(Set.of(1L));

        mockMvc.perform(post("/hospital-admission")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.date").value("Data da internação é obrigatória"))
                .andExpect(jsonPath("$.reasonHospitalization").value("Motivo da internação é obrigatório"))
                .andExpect(jsonPath("$.dateMedicalDischarge").value("Data de alta médica é obrigatória"))
                .andExpect(jsonPath("$.healthRecordId").value("ID do prontuário é obrigatório"));
    }
}
