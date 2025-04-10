package com.project.vetdata.service;

import com.project.vetdata.dto.HealthRecordCreateDTO;
import com.project.vetdata.model.HealthRecord;

public interface HealthRecordService {
    HealthRecord createHealthRecord(HealthRecordCreateDTO dto);
}
