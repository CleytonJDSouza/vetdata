package com.project.vetdata.repository;

import com.project.vetdata.model.DogBreed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DogBreedRepository extends JpaRepository<DogBreed, Long> {

    Optional<DogBreed> findByIdExternalApi(String idExternalApi);
}