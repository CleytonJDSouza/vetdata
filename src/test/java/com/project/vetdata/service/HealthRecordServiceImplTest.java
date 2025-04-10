package com.project.vetdata.service;

import com.project.vetdata.dto.HealthRecordCreateDTO;
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

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

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
}
