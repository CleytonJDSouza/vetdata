package com.project.vetdata.service;

import com.project.vetdata.dto.HealthRecordCreateDTO;
import com.project.vetdata.dto.HospitalAdmissionCreateDTO;
import com.project.vetdata.dto.HospitalAdmissionResponseDTO;
import com.project.vetdata.dto.HospitalAdmissionUpdateDTO;
import com.project.vetdata.model.HospitalAdmission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface HospitalAdmissionService {
    HospitalAdmissionResponseDTO createHospitalAdmission(HospitalAdmissionCreateDTO dto);
    HospitalAdmissionResponseDTO updateHospitalAdmission(Long id, HospitalAdmissionUpdateDTO dto);
    List<HospitalAdmissionResponseDTO> findAll();
    void deleteHospitalAdmission(Long id);
    Optional<HospitalAdmission> getHospitalAdmissionById(Long id);
}
