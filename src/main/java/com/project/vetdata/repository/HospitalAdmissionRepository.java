package com.project.vetdata.repository;

import com.project.vetdata.model.HospitalAdmission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HospitalAdmissionRepository extends JpaRepository<HospitalAdmission, Long> {

    @Query(value = "SELECT * FROM hospital_admission WHERE LOWER(reason_hospitalization) LIKE CONCAT('%', LOWER(:search), '%')",
            countQuery = "SELECT COUNT(*) FROM hospital_admission WHERE LOWER(reason_hospitalization) LIKE CONCAT('%', LOWER(:search), '%')",
            nativeQuery = true)
    Page<HospitalAdmission> searchByReason(@Param("search") String search, Pageable pageable);

    @EntityGraph(attributePaths = {"postOperatives", "diagnostics"})
    Optional<HospitalAdmission> findWithRelationsById(Long id);
}

