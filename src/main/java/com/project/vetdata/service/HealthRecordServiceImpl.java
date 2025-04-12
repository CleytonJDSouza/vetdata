package com.project.vetdata.service;

import com.project.vetdata.dto.HealthRecordCreateDTO;
import com.project.vetdata.model.DogBreed;
import com.project.vetdata.model.HealthRecord;
import com.project.vetdata.repository.DogBreedRepository;
import com.project.vetdata.repository.HealthRecordRepository;
import org.springframework.stereotype.Service;

@Service
public class HealthRecordServiceImpl implements HealthRecordService {

    private final HealthRecordRepository healthRecordRepository;
    private final DogBreedRepository dogBreedRepository;

    public HealthRecordServiceImpl(HealthRecordRepository healthRecordRepository, DogBreedRepository dogBreedRepository) {
        this.healthRecordRepository = healthRecordRepository;
        this.dogBreedRepository = dogBreedRepository;
    }

    public HealthRecord createHealthRecord(HealthRecordCreateDTO dto) {
        HealthRecord record = fromCreateDTO(dto);
        return healthRecordRepository.save(record);
    }

    private HealthRecord fromCreateDTO(HealthRecordCreateDTO dto) {
        DogBreed breed = dogBreedRepository.findById(dto.getBreedId())
                .orElseThrow(() -> new IllegalArgumentException("Raça " + dto.getBreedId() + " não encontrada"));

        HealthRecord record = new HealthRecord();
        record.setAdmission(dto.getAdmission());
        record.setAge(dto.getAge());
        record.setCodPatient(dto.getCodPatient());
        record.setColor(dto.getColor());
        record.setDeath(dto.getDeath());
        record.setEuthanasia(dto.getEuthanasia());
        record.setGender(dto.getGender());
        record.setPatient(dto.getPatient());
        record.setSize(dto.getSize());
        record.setTutor(dto.getTutor());
        record.setWeight(dto.getWeight());
        record.setBreed(breed);

        return record;
    }
}
