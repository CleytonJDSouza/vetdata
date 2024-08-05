package com.project.vetdata.service;

import com.project.vetdata.dto.DogBreedUpdateDTO;
import com.project.vetdata.exception.BreedNotFoundException;
import com.project.vetdata.model.DogBreed;
import com.project.vetdata.repository.DogBreedRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public Page<DogBreed> getAllDogBreeds(Pageable pageable) {
        return dogBreedRepository.findAll(pageable);
    }

    @Override
    public Optional<DogBreed> getDogBreedId(Long id) {
        return dogBreedRepository.findById(id);
    }

    @Override
    public DogBreed createDogBreed(DogBreed dogBreed) {
        return dogBreedRepository.save(dogBreed);
    }

    @Override
    public void deleteDogBreed(Long id) { dogBreedRepository.deleteById(id); }

    @Override
    public DogBreed updateDogBreed(Long id, DogBreedUpdateDTO dogBreedUpdateDTO) {
        return dogBreedRepository.findById(id).map(existingBreed -> {
            if (dogBreedUpdateDTO.getDescription() != null) {
                existingBreed.setDescription(dogBreedUpdateDTO.getDescription());}
            existingBreed.setLifeExpectancyMin(dogBreedUpdateDTO.getLifeExpectancyMin());
            existingBreed.setLifeExpectancyMax(dogBreedUpdateDTO.getLifeExpectancyMax());
            existingBreed.setMaleWeightMin(dogBreedUpdateDTO.getMaleWeightMin());
            existingBreed.setMaleWeightMax(dogBreedUpdateDTO.getMaleWeightMax());
            existingBreed.setFemaleWeightMin(dogBreedUpdateDTO.getFemaleWeightMin());
            existingBreed.setFemaleWeightMax(dogBreedUpdateDTO.getFemaleWeightMax());
            existingBreed.setHypoallergenic(dogBreedUpdateDTO.getHypoallergenic());
            existingBreed.setSize(dogBreedUpdateDTO.getSize());
            return dogBreedRepository.save(existingBreed);
        }).orElseThrow(() -> new BreedNotFoundException("Raça não encontrada!" + id));
    }
}
