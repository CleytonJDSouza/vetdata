package com.project.vetdata.service;

import com.project.vetdata.dto.HealthRecordCreateDTO;
import com.project.vetdata.dto.HospitalAdmissionCreateDTO;
import com.project.vetdata.dto.HospitalAdmissionResponseDTO;
import com.project.vetdata.model.HospitalAdmission;

public interface HospitalAdmissionService {
    HospitalAdmissionResponseDTO createHospitalAdmission(HospitalAdmissionCreateDTO dto);
}
