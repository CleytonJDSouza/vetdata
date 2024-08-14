package com.project.vetdata.service;

import com.project.vetdata.dto.DogBreedCreateDTO;
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
    public DogBreed createDogBreed(DogBreedCreateDTO dogBreedCreateDTO) {
        DogBreed newBreed = new DogBreed();
        newBreed.setName(dogBreedCreateDTO.getName());
        newBreed.setDescription(dogBreedCreateDTO.getDescription());
        newBreed.setLifeExpectancyMin(dogBreedCreateDTO.getLifeExpectancyMin());
        newBreed.setLifeExpectancyMax(dogBreedCreateDTO.getLifeExpectancyMax());
        newBreed.setMaleWeightMin(dogBreedCreateDTO.getMaleWeightMin());
        newBreed.setMaleWeightMax(dogBreedCreateDTO.getMaleWeightMax());
        newBreed.setFemaleWeightMin(dogBreedCreateDTO.getFemaleWeightMin());
        newBreed.setFemaleWeightMax(dogBreedCreateDTO.getFemaleWeightMax());
        newBreed.setHypoallergenic(dogBreedCreateDTO.getHypoallergenic());
        newBreed.setSize(dogBreedCreateDTO.getSize());
        return dogBreedRepository.save(newBreed);
    }

    @Override
    public void deleteDogBreed(Long id) { dogBreedRepository.deleteById(id); }

    @Override
    public DogBreed updateDogBreed(Long id, DogBreedUpdateDTO dogBreedUpdateDTO) {
        return dogBreedRepository.findById(id).map(existingBreed -> {
            if (dogBreedUpdateDTO.getDescription() != null) {
                existingBreed.setDescription(dogBreedUpdateDTO.getDescription());
            }
            if (dogBreedUpdateDTO.getLifeExpectancyMin() != null) {
                existingBreed.setLifeExpectancyMin(dogBreedUpdateDTO.getLifeExpectancyMin());
            }
            if (dogBreedUpdateDTO.getLifeExpectancyMax() != null) {
                existingBreed.setLifeExpectancyMax(dogBreedUpdateDTO.getLifeExpectancyMax());
            }
            if (dogBreedUpdateDTO.getMaleWeightMin() != null) {
                existingBreed.setMaleWeightMin(dogBreedUpdateDTO.getMaleWeightMin());
            }
            if (dogBreedUpdateDTO.getMaleWeightMax() != null) {
                existingBreed.setMaleWeightMax(dogBreedUpdateDTO.getMaleWeightMax());
            }
            if (dogBreedUpdateDTO.getFemaleWeightMin() != null) {
                existingBreed.setFemaleWeightMin(dogBreedUpdateDTO.getFemaleWeightMin());
            }
            if (dogBreedUpdateDTO.getFemaleWeightMax() != null) {
                existingBreed.setFemaleWeightMax(dogBreedUpdateDTO.getFemaleWeightMax());
            }
            if (dogBreedUpdateDTO.getHypoallergenic() != null) {
                existingBreed.setHypoallergenic(dogBreedUpdateDTO.getHypoallergenic());
            }
            if (dogBreedUpdateDTO.getSize() != null) {
                existingBreed.setSize(dogBreedUpdateDTO.getSize());
            }
            return dogBreedRepository.save(existingBreed);
        }).orElseThrow(() -> new BreedNotFoundException("Raça não encontrada!" + id));
    }
}
