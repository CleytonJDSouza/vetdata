package com.project.vetdata.service;

import com.project.vetdata.dto.DogBreedUpdateDTO;
import com.project.vetdata.model.DogBreed;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface DogBreedService {
        Page<DogBreed> getAllDogBreeds(Pageable pageable);
        Optional<DogBreed> getDogBreedId(Long id);
        DogBreed createDogBreed(DogBreed dogBreed);
        void deleteDogBreed(Long id);
        DogBreed updateDogBreed(Long id, DogBreedUpdateDTO dogBreedUpdateDTO);
}
