package com.project.vetdata.service;

import com.project.vetdata.dto.HealthRecordCreateDTO;
import com.project.vetdata.dto.HealthRecordUpdateDTO;
import com.project.vetdata.model.HealthRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface HealthRecordService {
    HealthRecord createHealthRecord(HealthRecordCreateDTO dto);
    HealthRecord updateHealthRecord(Long id, HealthRecordUpdateDTO dto);
    Page<HealthRecord> findAll(Pageable pageable);
    Page<HealthRecord> searchByPatient(String search, Pageable pageable);
}
