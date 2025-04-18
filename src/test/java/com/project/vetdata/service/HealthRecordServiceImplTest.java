package com.project.vetdata.service;

import com.project.vetdata.dto.HealthRecordCreateDTO;
import com.project.vetdata.dto.HealthRecordUpdateDTO;
import com.project.vetdata.enums.DogSize;
import com.project.vetdata.enums.Euthanasia;
import com.project.vetdata.enums.Gender;
import com.project.vetdata.model.DogBreed;
import com.project.vetdata.model.HealthRecord;
import com.project.vetdata.repository.DogBreedRepository;
import com.project.vetdata.repository.HealthRecordRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HealthRecordServiceImplTest {

    @InjectMocks
    private HealthRecordServiceImpl healthRecordService;

    @Mock
    private HealthRecordRepository healthRecordRepository;

    @Mock
    private DogBreedRepository dogBreedRepository;

    @Test
    public void given_valid_healthRecordCreateDTO_when_createHealthRecord_is_called_then_healthRecord_is_saved_and_returned() {
        HealthRecordCreateDTO dto = getFakeHealthRecordCreateDTO();
        DogBreed breed = getFakeDogBreed();
        HealthRecord record = getFakeHealthRecord();

        when(dogBreedRepository.findById(dto.getBreedId())).thenReturn(Optional.of(breed));
        when(healthRecordRepository.save(any(HealthRecord.class))).thenReturn(record);

        HealthRecord savedRecord = healthRecordService.createHealthRecord(dto);

        assertEquals(dto.getCodPatient(), savedRecord.getCodPatient());
        assertEquals(dto.getTutor(), savedRecord.getTutor());
        assertEquals(dto.getPatient(), savedRecord.getPatient());
        assertEquals(dto.getBreedId(), savedRecord.getBreed().getId());
        assertEquals(dto.getAge(), savedRecord.getAge());
    }

    @Test
    public void given_invalid_breed_id_when_createHealthRecord_then_throws_exception() {
        HealthRecordCreateDTO dto = getFakeHealthRecordCreateDTO();
        when(dogBreedRepository.findById(dto.getBreedId())).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            healthRecordService.createHealthRecord(dto);
        });

        assertEquals("Raça " + dto.getBreedId() + " não encontrada", exception.getMessage());
    }

    @Test
    public void given_valid_id_and_healthRecordUpdateDTO_when_updateHealthRecord_is_called_then_healthRecord_is_updated_and_saved() {
        Long id = 1L;
        HealthRecord existingRecord = getFakeHealthRecord();
        HealthRecordUpdateDTO updateDTO = getFakeHealthRecordUpdateDTO();
        DogBreed breed = getFakeDogBreed();

        when(healthRecordRepository.findById(id)).thenReturn(Optional.of(existingRecord));
        when(dogBreedRepository.findById(updateDTO.getBreedId())).thenReturn(Optional.of(breed));
        when(healthRecordRepository.save(any(HealthRecord.class))).thenReturn(existingRecord);

        HealthRecord updateRecord = healthRecordService.updateHealthRecord(id, updateDTO);

        verify(healthRecordRepository, times(1)).findById(id);
        verify(dogBreedRepository, times(1)).findById(updateDTO.getBreedId());
        verify(healthRecordRepository, times(1)).save(existingRecord);

        assertEquals(updateDTO.getPatient(), updateRecord.getPatient());
        assertEquals(updateDTO.getTutor(), updateRecord.getTutor());
        assertEquals(updateDTO.getCodPatient(), updateRecord.getCodPatient());
        assertEquals(updateDTO.getBreedId(), updateRecord.getBreed().getId());
        assertEquals(updateDTO.getWeight(), updateRecord.getWeight());
        assertEquals(updateDTO.getSize(), updateRecord.getSize());
        assertEquals(updateDTO.getColor(), updateRecord.getColor());
    }

    @Test
    public void given_invalid_id_when_updateHealthRecord_is_called_then_throws_exception() {
        Long invalidId = 99L;
        HealthRecordUpdateDTO updateDTO = getFakeHealthRecordUpdateDTO();

        when(healthRecordRepository.findById(invalidId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                healthRecordService.updateHealthRecord(invalidId, updateDTO)
        );

        assertEquals("Prontuário não encontrado com ID: " + invalidId, exception.getMessage());
    }

    @Test
    public void given_invalid_breedId_when_updateHealthRecord_is_called_then_throws_exception() {
        Long id = 1L;
        HealthRecord existingRecord = getFakeHealthRecord();
        HealthRecordUpdateDTO updateDTO = getFakeHealthRecordUpdateDTO();

        when(healthRecordRepository.findById(id)).thenReturn(Optional.of(existingRecord));
        when(dogBreedRepository.findById(updateDTO.getBreedId())).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                healthRecordService.updateHealthRecord(id, updateDTO)
        );

        assertEquals("Raça " + updateDTO.getBreedId() + " não encontrada", exception.getMessage());
    }

    @Test
    public void given_null_fields_when_updateHealthRecord_then_no_fields_are_updated() {
        Long id = 1L;
        HealthRecord existing = getFakeHealthRecord();

        when(healthRecordRepository.findById(id)).thenReturn(Optional.of(existing));
        when(healthRecordRepository.save(any(HealthRecord.class))).thenAnswer(invocation -> invocation.getArgument(0));

        HealthRecordUpdateDTO dto = new HealthRecordUpdateDTO();

        HealthRecord updated = healthRecordService.updateHealthRecord(id, dto);

        assertEquals(existing.getPatient(), updated.getPatient());
        verify(healthRecordRepository).save(existing);
    }

    @Test
    public void given_healthRecords_pre_registered_when_a_page_is_informed_then_the_records_are_returned() {
        Pageable pageable = PageRequest.of(0, 10);
        HealthRecord healthRecord = getFakeHealthRecord();
        Page<HealthRecord> page = new PageImpl<>(Collections.singletonList(healthRecord));

        when(healthRecordRepository.findAll(pageable)).thenReturn(page);

        Page<HealthRecord> records = healthRecordService.findAll(pageable);

        assertNotNull(records);
        assertFalse(records.isEmpty(), "A página não deve estar vazia!");
        assertEquals(1, records.getContent().size(), "Deve haver exatamente 1 registro na página");
        assertEquals("Torresmo", records.getContent().get(0).getPatient());
    }

    @Test
    public void given_no_healthRecord_when_a_page_is_requested_then_should_return_empty_page() {
        Pageable pageable = PageRequest.of(0,10);
        Page<HealthRecord> emptyPage = Page.empty();

        when(healthRecordRepository.findAll(pageable)).thenReturn(emptyPage);

        Page<HealthRecord> result = healthRecordService.findAll(pageable);

        assertNotNull(result);
        assertTrue(result.isEmpty(), "A página deve estar vazia");
    }

    private HealthRecordCreateDTO getFakeHealthRecordCreateDTO() {
        HealthRecordCreateDTO dto = new HealthRecordCreateDTO();
        dto.setCodPatient("P01");
        dto.setTutor("Cleyton");
        dto.setPatient("Torresmo");
        dto.setBreedId(1L);
        dto.setAge(5.0);
        dto.setWeight(22.0);
        dto.setColor("Bege");
        dto.setSize(DogSize.MEDIUM);
        dto.setGender(Gender.MALE);
        dto.setDeath(null);
        dto.setEuthanasia(Euthanasia.NO);
        dto.setAdmission(LocalDate.of(2025,1,1));

        return dto;
    }

    public DogBreed getFakeDogBreed() {
        return new DogBreed(1L, "2", "Golden Retriever", "Amigável e inteligente e esperto",
                10, 12, 30D, 34D, 25D, 29D,
                false, "Medio");
    }

    private HealthRecord getFakeHealthRecord() {
        HealthRecord record = new HealthRecord();
        record.setCodPatient("P01");
        record.setTutor("Cleyton");
        record.setPatient("Torresmo");
        record.setBreed(getFakeDogBreed());
        record.setAge(5.0);
        record.setWeight(22.0);
        record.setColor("Bege");
        record.setSize(DogSize.MEDIUM);
        record.setGender(Gender.MALE);
        record.setDeath(null);
        record.setEuthanasia(Euthanasia.NO);
        record.setAdmission(LocalDate.of(2025, 1, 1));

        return record;
    }

    private HealthRecordUpdateDTO getFakeHealthRecordUpdateDTO() {
        HealthRecordUpdateDTO dto = new HealthRecordUpdateDTO();
        dto.setCodPatient("P02");
        dto.setTutor("Andrea");
        dto.setPatient("Cacau");
        dto.setBreedId(1L);
        dto.setAge(3.5);
        dto.setWeight(18.0);
        dto.setColor("Preto");
        dto.setSize(DogSize.SMALL);
        dto.setGender(Gender.FEMALE);
        dto.setDeath(false);
        dto.setEuthanasia(Euthanasia.NO);
        dto.setAdmission(LocalDate.of(2025, 2, 1));

        return dto;
    }
}
