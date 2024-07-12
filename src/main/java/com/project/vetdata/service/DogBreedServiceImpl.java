package com.project.vetdata.service;

import com.project.vetdata.model.DogBreed;
import com.project.vetdata.repository.DogBreedRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DogBreedServiceImpl implements DogBreedService {

    private final DogBreedRepository dogBreedRepository;

    @Autowired
    public DogBreedServiceImpl(DogBreedRepository dogBreedRepository) {
        this.dogBreedRepository = dogBreedRepository;
    }

    @Override
    public List<DogBreed> getAllDogBreeds() {
        return dogBreedRepository.findAll();
    }

    @Override
    public Optional<DogBreed> getDogBreedId(Long id) {
        return dogBreedRepository.findById(id);
    }

    @Override
    public DogBreed createDogBreed(DogBreed dogBreed) {
        return dogBreedRepository.save(dogBreed);
    }
}
