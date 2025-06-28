package com.project.vetdata.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.vetdata.configs.TestSecurityConfig;
import com.project.vetdata.dto.HospitalAdmissionCreateDTO;
import com.project.vetdata.dto.HospitalAdmissionResponseDTO;
import com.project.vetdata.dto.HospitalAdmissionUpdateDTO;
import com.project.vetdata.enums.MedicalEvolution;
import com.project.vetdata.enums.ReasonHospitalization;
import com.project.vetdata.enums.TreatmentNextSteps;
import com.project.vetdata.service.HospitalAdmissionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HospitalAdmissionController.class)
@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
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
        dto.setReasonHospitalization(ReasonHospitalization.DERMATOLOGIA);
        dto.setDateMedicalDischarge(LocalDate.of(2024, 3, 4));
        dto.setDateReturns(LocalDate.of(2024, 4, 17));
        dto.setMedicalEvolution(MedicalEvolution.PROGRESSO_DESFAVORAVEL);
        dto.setTreatmentNextSteps(TreatmentNextSteps.EUTANASIA);
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
                .andExpect(jsonPath("$.reasonHospitalization").value(ReasonHospitalization.DERMATOLOGIA.name()))
                .andExpect(jsonPath("$.medicalEvolution").value(MedicalEvolution.PROGRESSO_DESFAVORAVEL.name()))
                .andExpect(jsonPath("$.treatmentNextSteps").value(TreatmentNextSteps.EUTANASIA.name()));
    }

    @Test
    public void given_invalid_hospitalAdmission_when_createHospitalAdmission_then_returns_badRequest() throws Exception {
        HospitalAdmissionCreateDTO dto = new HospitalAdmissionCreateDTO();
        dto.setDate(null);
        dto.setReasonHospitalization(null);
        dto.setDateMedicalDischarge(null);
        dto.setDateReturns(LocalDate.of(2024, 4, 17));
        dto.setMedicalEvolution(null);
        dto.setTreatmentNextSteps(null);
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

    @Test
    public void given_hospitalAdmission_exists_and_is_updated_when_updateHospitalAdmission_then_returns_updateAdmission() throws Exception {
        HospitalAdmissionUpdateDTO updateDTO = new HospitalAdmissionUpdateDTO();
        updateDTO.setDate(LocalDate.of(2025, 6, 12));
        updateDTO.setReasonHospitalization(ReasonHospitalization.DERMATOLOGIA);
        updateDTO.setDateMedicalDischarge(LocalDate.of(2025, 7, 12));
        updateDTO.setDateReturns(LocalDate.of(2025, 8, 12));
        updateDTO.setMedicalEvolution(MedicalEvolution.QUADRO_GRAVE);
        updateDTO.setTreatmentNextSteps(TreatmentNextSteps.EUTANASIA);
        updateDTO.setPostOperativeIds(Set.of(1L, 2L));
        updateDTO.setDiagnosticIds(Set.of(3L));
        updateDTO.setHealthRecordId(1L);

        HospitalAdmissionResponseDTO updatedAdmission = new HospitalAdmissionResponseDTO();
        updatedAdmission.setId(123L);
        updatedAdmission.setDate(updateDTO.getDate());
        updatedAdmission.setReasonHospitalization(updateDTO.getReasonHospitalization());
        updatedAdmission.setDateMedicalDischarge(updateDTO.getDateMedicalDischarge());
        updatedAdmission.setDateReturns(updateDTO.getDateReturns());
        updatedAdmission.setMedicalEvolution(updateDTO.getMedicalEvolution());
        updatedAdmission.setTreatmentNextSteps(updateDTO.getTreatmentNextSteps());

        when(hospitalAdmissionService.updateHospitalAdmission(eq(123L), any(HospitalAdmissionUpdateDTO.class)))
                .thenReturn(updatedAdmission);

        mockMvc.perform(put("/hospital-admission/123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(123))
                .andExpect(jsonPath("$.reasonHospitalization").value(ReasonHospitalization.DERMATOLOGIA.name()))
                .andExpect(jsonPath("$.medicalEvolution").value(MedicalEvolution.QUADRO_GRAVE.name()))
                .andExpect(jsonPath("$.treatmentNextSteps").value(TreatmentNextSteps.EUTANASIA.name()))
                .andExpect(jsonPath("$.date").value("2025-06-12"))
                .andExpect(jsonPath("$.dateMedicalDischarge").value("2025-07-12"))
                .andExpect(jsonPath("$.dateReturns").value("2025-08-12"));
    }

    @Test
    public void given_invalid_hospitalAdmission_when_updateHospitalAdmission_then_returns_badRequest() throws Exception {
        HospitalAdmissionUpdateDTO updateDTO = new HospitalAdmissionUpdateDTO();
        updateDTO.setDate(null);
        updateDTO.setReasonHospitalization(null);
        updateDTO.setDateMedicalDischarge(null);
        updateDTO.setDateReturns(null);
        updateDTO.setMedicalEvolution(null);
        updateDTO.setTreatmentNextSteps(null);
        updateDTO.setPostOperativeIds(null);
        updateDTO.setDiagnosticIds(null);

        mockMvc.perform(put("/hospital-admission/123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.date").value("Campo Obrigatório"))
                .andExpect(jsonPath("$.reasonHospitalization").value("Campo Obrigatório"))
                .andExpect(jsonPath("$.medicalEvolution").value("Campo Obrigatório"))
                .andExpect(jsonPath("$.treatmentNextSteps").value("Campo Obrigatório"))
                .andExpect(jsonPath("$.postOperativeIds").value("Campo Obrigatório"))
                .andExpect(jsonPath("$.diagnosticIds").value("Campo Obrigatório"));

    }
}
