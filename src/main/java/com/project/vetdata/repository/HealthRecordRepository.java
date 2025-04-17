package com.project.vetdata.repository;

import com.project.vetdata.model.HealthRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface HealthRecordRepository extends JpaRepository<HealthRecord, Long> {

    @Query(value = "SELECT * FROM health_records WHERE LOWER(patient) LIKE CONCAT('%', LOWER(:search), '%')",
            countQuery = "SELECT COUNT(*) FROM health_records WHERE LOWER(patient) LIKE CONCAT('%', LOWER(:search), '%')",
            nativeQuery = true
    )
    Page<HealthRecord> searchByPatient(@Param("search") String search, Pageable pageable);

}
