package com.project.vetdata.service;

import com.project.vetdata.dto.HealthRecordCreateDTO;
import com.project.vetdata.dto.HealthRecordUpdateDTO;
import com.project.vetdata.model.DogBreed;
import com.project.vetdata.model.HealthRecord;
import com.project.vetdata.repository.DogBreedRepository;
import com.project.vetdata.repository.HealthRecordRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

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

    public HealthRecord updateHealthRecord(Long id, HealthRecordUpdateDTO dto) {
        return healthRecordRepository.findById(id).map(existingRecord -> {
            if (dto.getAdmission() != null) {
                existingRecord.setAdmission(dto.getAdmission());
            }
            if (dto.getAge() != null) {
                existingRecord.setAge(dto.getAge());
            }
            if (dto.getCodPatient() != null) {
                existingRecord.setCodPatient(dto.getCodPatient());
            }
            if (dto.getColor() != null) {
                existingRecord.setColor(dto.getColor());
            }
            if (dto.getDeath() != null) {
                existingRecord.setDeath(dto.getDeath());
            }
            if (dto.getEuthanasia() != null) {
                existingRecord.setEuthanasia(dto.getEuthanasia());
            }
            if (dto.getGender() != null) {
                existingRecord.setGender(dto.getGender());
            }
            if (dto.getPatient() != null) {
                existingRecord.setPatient(dto.getPatient());
            }
            if (dto.getSize() != null) {
                existingRecord.setSize(dto.getSize());
            }
            if (dto.getTutor() != null) {
                existingRecord.setTutor(dto.getTutor());
            }
            if (dto.getWeight() != null) {
                existingRecord.setWeight(dto.getWeight());
            }
            if (dto.getBreedId() != null) {
                DogBreed breed = dogBreedRepository.findById(dto.getBreedId())
                        .orElseThrow(() -> new IllegalArgumentException("Raça " + dto.getBreedId() + " não encontrada"));
                existingRecord.setBreed(breed);
            }

            return healthRecordRepository.save(existingRecord);
        }).orElseThrow(() -> new IllegalArgumentException("Prontuário não encontrado com ID: " + id));
    }

    @Override
    public Page<HealthRecord> findAll(Pageable pageable) {
        return healthRecordRepository.findAll(pageable);
    }

    @Override
    public Page<HealthRecord> searchByPatient(String search, Pageable pageable) {
        return healthRecordRepository.searchByPatient(search, pageable);
    }

    @Override
    public void deleteHealthRecord(Long id) {
        healthRecordRepository.deleteById(id);
    }

    @Override
    public Optional<HealthRecord> getHealthRecordById(Long id) {
        return healthRecordRepository.findById(id);
    }
}
