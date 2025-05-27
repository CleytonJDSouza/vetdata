package com.project.vetdata.repository;

import com.project.vetdata.model.PostOperative;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;

@Repository
public interface PostOperativeRepository extends JpaRepository<PostOperative, Long> {
    Page<PostOperative> findByDescriptionContainingIgnoreCase(String term, Pageable pageable);
}
