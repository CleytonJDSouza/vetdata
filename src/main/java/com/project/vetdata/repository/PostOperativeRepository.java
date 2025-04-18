package com.project.vetdata.repository;

import com.project.vetdata.model.Diagnostic;
import com.project.vetdata.model.PostOperative;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostOperativeRepository extends JpaRepository<PostOperative, Long> {
}
