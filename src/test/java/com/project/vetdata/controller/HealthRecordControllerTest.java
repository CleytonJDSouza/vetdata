package com.project.vetdata.controller;

import com.project.vetdata.dto.HealthRecordCreateDTO;
import com.project.vetdata.dto.HealthRecordResponseDTO;
import com.project.vetdata.dto.HealthRecordUpdateDTO;
import com.project.vetdata.enums.DogSize;
import com.project.vetdata.enums.Euthanasia;
import com.project.vetdata.enums.Gender;
import com.project.vetdata.model.DogBreed;
import com.project.vetdata.model.HealthRecord;
import com.project.vetdata.service.HealthRecordService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;




import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
        HealthRecordCreateDTO dto = new HealthRecordCreateDTO(3.5, "C123", "Preto", false, Euthanasia.NO, Gender.MALE, "Torresmo", DogSize.SMALL, "Cleyton", 12.0, 1L);

        HealthRecord savedRecord = new HealthRecord(10L, dto.getAge(), dto.getCodPatient(), dto.getColor(), dto.getDeath(), dto.getEuthanasia(),
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
                .andExpect(jsonPath("$.euthanasia").value("Campo Obrigatório"))
                .andExpect(jsonPath("$.gender").value("Campo Obrigatório"))
                .andExpect(jsonPath("$.patient").value("Campo Obrigatório"))
                .andExpect(jsonPath("$.size").value("Campo Obrigatório"))
                .andExpect(jsonPath("$.tutor").value("Campo Obrigatório"))
                .andExpect(jsonPath("$.weight").value("Campo deve ter valor maior que 0"))
                .andExpect(jsonPath("$.breedId").value("Campo Obrigatório"));
    }

    @Test
    public void given_healthRecord_exists_and_is_updated_when_updateHealthRecord_then_returns_updatedRecord() throws Exception {
        HealthRecordUpdateDTO updateDTO = new HealthRecordUpdateDTO(
                4.5, "P145", "Branco",
                false, Euthanasia.NO, Gender.FEMALE, "Cacau", DogSize.MEDIUM,
                "Andrea", 14.0, 2L
        );

        HealthRecord updatedRecord = new HealthRecord(
                123L,
                updateDTO.getAge(),
                updateDTO.getCodPatient(),
                updateDTO.getColor(),
                updateDTO.getDeath(),
                updateDTO.getEuthanasia(),
                updateDTO.getGender(),
                updateDTO.getPatient(),
                updateDTO.getSize(),
                updateDTO.getTutor(),
                updateDTO.getWeight(),
                new DogBreed(2L, "456", "Poodle", "Inteligente e Ativo", 12, 15,
                        10.0, 12.0, 8.0, 10.0, false, "Pequeno")
        );

        when(healthRecordService.updateHealthRecord(eq(123L), any(HealthRecordUpdateDTO.class)))
                .thenReturn(updatedRecord);

        mockMvc.perform(put("/health-records/123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(123))
                .andExpect(jsonPath("$.codPatient").value("P145"))
                .andExpect(jsonPath("$.patient").value("Cacau"))
                .andExpect(jsonPath("$.tutor").value("Andrea"))
                .andExpect(jsonPath("$.breedId").value(2))
                .andExpect(jsonPath("$.breedName").value("Poodle"))
                .andExpect(jsonPath("$.age").value(4.5))
                .andExpect(jsonPath("$.color").value("Branco"))
                .andExpect(jsonPath("$.euthanasia").value("NO"))
                .andExpect(jsonPath("$.gender").value("FEMALE"))
                .andExpect(jsonPath("$.size").value("MEDIUM"))
                .andExpect(jsonPath("$.weight").value(14.0))
                .andExpect(jsonPath("$.death").value(false));
    }

    @Test
    public void given_invalid_healthRecord_when_updateHealthRecord_then_returnsBadRequest() throws Exception {
        HealthRecordUpdateDTO updateDTO = new HealthRecordUpdateDTO();
        updateDTO.setAge(-2.0);
        updateDTO.setCodPatient("");
        updateDTO.setColor("");
        updateDTO.setDeath(null);
        updateDTO.setEuthanasia(null);
        updateDTO.setGender(null);
        updateDTO.setPatient("");
        updateDTO.setSize(null);
        updateDTO.setTutor("");
        updateDTO.setWeight(-10.0);
        updateDTO.setBreedId(null);

        mockMvc.perform(put("/health-records/123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.euthanasia").value("Campo Obrigatório"))
                .andExpect(jsonPath("$.gender").value("Campo Obrigatório"))
                .andExpect(jsonPath("$.patient").value("Campo Obrigatório"))
                .andExpect(jsonPath("$.size").value("Campo Obrigatório"))
                .andExpect(jsonPath("$.tutor").value("Campo Obrigatório"))
                .andExpect(jsonPath("$.weight").value("Campo deve ter valor maior que 0"))
                .andExpect(jsonPath("$.breedId").value("Campo Obrigatório"));
    }

    @Test
    public void given_healthRecordDoesNotExist_when_updateHealthRecord_then_returnsNotFound() throws Exception {
        HealthRecordUpdateDTO updateDTO = new HealthRecordUpdateDTO(4.5, "C456", "Branco",
                false, Euthanasia.NO, Gender.FEMALE, "Cookie", DogSize.MEDIUM, "Aline", 14.0, 2L);

        when(healthRecordService.updateHealthRecord(eq(999L), any(HealthRecordUpdateDTO.class)))
                .thenThrow(new EntityNotFoundException("Prontuário não encontrado"));

        mockMvc.perform(put("/health-records/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void given_pageRequestAndHealthRecordsExist_when_getHealthRecordsPage_then_returnsHealthRecordsPage() throws Exception {
        List<HealthRecord> records = List.of(
                new HealthRecord(1L, 3.0, "C001", "Preto", false, Euthanasia.NO,
                        Gender.FEMALE, "Cacau", DogSize.SMALL, "Andrea", 10.0,
                        new DogBreed(1L, "001", "Beagle", "Curioso", 10, 12,
                                10.0, 15.0, 8.0, 12.0, false, "Pequeno")),
                new HealthRecord(2L, 4.0, "C002", "Marrom", false, Euthanasia.NO,
                        Gender.MALE, "Nutella", DogSize.MEDIUM, "Aline", 14.0,
                        new DogBreed(2L, "002", "Boxer", "Brincalhão", 9, 11,
                                25.0, 32.0, 22.0, 30.0, false, "Médio"))
        );

        Pageable paging = PageRequest.of(0, 2, Sort.by("id"));
        Page<HealthRecord> pageHealthRecords = new PageImpl<>(records, paging, records.size());

        when(healthRecordService.findAll(Mockito.any(Pageable.class))).thenReturn(pageHealthRecords);

        mockMvc.perform(get("/health-records?page=0&qtdRecordsPage=2&sortBy=id")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(records.size()))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[1].id").value(2));
    }

    @Test
    public void given_no_healthRecords_exist_when_get_healthRecords_page_then_returns_noContent() throws Exception {
        Pageable paging = PageRequest.of(0, 10, Sort.by("id"));
        Page<HealthRecord> emptyPage = new PageImpl<>(List.of(), paging, 0);

        when(healthRecordService.findAll(Mockito.any(Pageable.class))).thenReturn(emptyPage);

        mockMvc.perform(get("/health-records?page=0&qtdRecordsPage=10&sortBy=id")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    public void given_search_parameter_when_get_healthRecords_page_then_returns_filtered_healthRecords_page() throws Exception {
        List<HealthRecord> filteredRecords = List.of(
                new HealthRecord(3L, 2.0, "C003", "Branco", false, Euthanasia.NO,
                        Gender.FEMALE, "Cacau", DogSize.SMALL, "Andrea", 11.0,
                        new DogBreed(3L, "003", "Shih Tzu", "Fofo e tranquilo", 10, 13,
                                6.0, 8.0, 5.0, 7.0, false, "Pequeno"))
        );

        Pageable paging = PageRequest.of(0, 10, Sort.by("id"));
        Page<HealthRecord> pageHealthRecords = new PageImpl<>(filteredRecords, paging, filteredRecords.size());

        when(healthRecordService.searchByPatient(eq("Cacau"), any(Pageable.class)))
                .thenReturn(pageHealthRecords);

        mockMvc.perform(get("/health-records")
                        .param("page", "0")
                        .param("qtdRecordsPage", "10")
                        .param("sortBy", "id")
                        .param("search", "Cacau")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(1))
                .andExpect(jsonPath("$.data[0].id").value(3))
                .andExpect(jsonPath("$.data[0].patient").value("Cacau"));
    }

    @Test
    public void given_existing_healthRecordId_when_deleteHealthRecord_then_returns_noContent() throws Exception{
        Long healthRecordId = 1L;
        HealthRecord existingRecord = new HealthRecord(healthRecordId,
                4.0,"C357","Preto", false, Euthanasia.NO, Gender.MALE, "Nutella", DogSize.MEDIUM, "Clovis", 12.0,
                new DogBreed(1L, "001", "Labrador", "Companheiro", 10, 12,
                        25.0, 30.0, 22.0, 28.0, false, "Médio"));

        when(healthRecordService.getHealthRecordById(healthRecordId)).thenReturn(Optional.of(existingRecord));

        mockMvc.perform(delete("/health-records/{id}", healthRecordId))
                .andExpect(status().isNoContent());

        verify(healthRecordService,times(1)).deleteHealthRecord(healthRecordId);
    }

    @Test
    public void given_nonexistent_healthRecordId_when_deleteHealthRecord_then_returns_notFound() throws Exception {
        Long invalidId = 999L;

        when(healthRecordService.getHealthRecordById(invalidId)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/health-records/{id}", invalidId))
                .andExpect(status().isNotFound());
    }

    @Test
    public void given_existing_heathRecordId_when_getHealthRecordById_then_returns_record() throws Exception{
        Long healthRecordId = 1L;
        HealthRecord existingRecord = new HealthRecord(healthRecordId,
                4.0,"C357","Preto", false, Euthanasia.NO, Gender.MALE, "Nutella", DogSize.MEDIUM, "Clovis", 12.0,
                new DogBreed(1L, "001", "Labrador", "Companheiro", 10, 12,
                        25.0, 30.0, 22.0, 28.0, false, "Médio"));

        when(healthRecordService.getHealthRecordById(healthRecordId)).thenReturn(Optional.of(existingRecord));

        mockMvc.perform(get("/health-records/{id}", healthRecordId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(healthRecordId))
                .andExpect(jsonPath("$.patient").value("Nutella"));

    }

    @Test
    public void given_nonexistent_healthRecordId_when_getHealthRecordById_then_returns_notFound() throws Exception {
        Long id = 999L;

        when(healthRecordService.getHealthRecordById(id)).thenThrow(new EntityNotFoundException("Prontuário não encontrado"));

        mockMvc.perform(get("/health-records/{id}", id))
                .andExpect(status().isNotFound());
    }
}
