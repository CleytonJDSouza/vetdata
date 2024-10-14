package com.project.vetdata.repository;

import com.project.vetdata.model.DogBreed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DogBreedRepository extends JpaRepository<DogBreed, Long> {

    List<DogBreed> findByIdExternalApi(@Param("idExternalApi") String idExternalApi);
}