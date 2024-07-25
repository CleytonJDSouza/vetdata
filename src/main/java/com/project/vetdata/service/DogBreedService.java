package com.project.vetdata.service;

import com.project.vetdata.model.DogBreed;

import java.util.List;
import java.util.Optional;

public interface DogBreedService {
        List<DogBreed> getAllDogBreeds();
        Optional<DogBreed> getDogBreedId(Long id);
        DogBreed createDogBreed(DogBreed dogBreed);
        void deleteDogBreed(Long id);
}
