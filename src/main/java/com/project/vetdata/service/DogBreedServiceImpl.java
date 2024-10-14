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
        DogBreed newBreed = fromCreateDTO(dogBreedCreateDTO);
        return dogBreedRepository.save(newBreed);
    }

    @Override
    public void deleteDogBreed(Long id) { dogBreedRepository.deleteById(id); }

    @Override
    public List<DogBreed> findByExternalApi(String idExternalApi) {
        return dogBreedRepository.findByIdExternalApi(idExternalApi);
    }

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

    private DogBreed fromCreateDTO(DogBreedCreateDTO dto) {
        DogBreed breed = new DogBreed();
        breed.setName(dto.getName());
        breed.setDescription(dto.getDescription());
        breed.setLifeExpectancyMin(dto.getLifeExpectancyMin());
        breed.setLifeExpectancyMax(dto.getLifeExpectancyMax());
        breed.setMaleWeightMin(dto.getMaleWeightMin());
        breed.setMaleWeightMax(dto.getMaleWeightMax());
        breed.setFemaleWeightMin(dto.getFemaleWeightMin());
        breed.setFemaleWeightMax(dto.getFemaleWeightMax());
        breed.setHypoallergenic(dto.getHypoallergenic());
        breed.setSize(dto.getSize());
        breed.setIdExternalApi(null);
        return breed;
    }
}
