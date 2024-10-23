package com.project.vetdata.service;

import com.project.vetdata.dto.*;
import com.project.vetdata.model.DogBreed;
import com.project.vetdata.repository.DogBreedRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class DogBreedExternalService {

    private final RestTemplate restTemplate;
    private final String apiUrl;
    private final DogBreedRepository dogBreedRepository;

    @Autowired
    public DogBreedExternalService(RestTemplate restTemplate, @Value("${external.api.url}") String apiUrl, DogBreedRepository dogBreedRepository) {
        this.restTemplate = restTemplate;
        this.apiUrl = apiUrl;
        this.dogBreedRepository = dogBreedRepository;
    }

    public void saveFromExternalAPI(List<DogBreedExternalDTO> externalDTOs) {
        for (DogBreedExternalDTO dto : externalDTOs) {
            DogBreed breed = fromExternalDTO(dto);
            dogBreedRepository.save(breed);
        }
    }

    public List<DogBreedExternalDTO> getBreedsByPage(int pageNumber) {
        String url = apiUrl + "?page[number]=" + pageNumber;
        DogBreedResponseDTO response = restTemplate.getForObject(url, DogBreedResponseDTO.class);

        if (response != null && response.getData() != null) {
            return response.getData();
        }
        return new ArrayList<>();
    }

    public DogBreed fromExternalDTO(DogBreedExternalDTO dto) {
        DogBreed breed = new DogBreed();
        breed.setIdExternalApi(dto.getIdExternalApi());
        breed.setName(dto.getAttributeDTO().getName());
        breed.setDescription(dto.getAttributeDTO().getDescription());
        breed.setLifeExpectancyMin(dto.getAttributeDTO().getLife().getMin());
        breed.setLifeExpectancyMax(dto.getAttributeDTO().getLife().getMax());
        breed.setMaleWeightMin(dto.getAttributeDTO().getMaleWeightDTO().getMin());
        breed.setMaleWeightMax(dto.getAttributeDTO().getMaleWeightDTO().getMax());
        breed.setFemaleWeightMin(dto.getAttributeDTO().getFemaleWeightDTO().getMin());
        breed.setFemaleWeightMax(dto.getAttributeDTO().getFemaleWeightDTO().getMax());
        breed.setHypoallergenic(dto.getAttributeDTO().getHypoallergenic());
        breed.setSize(null);
        return breed;
    }

    public DogBreedUpdateDTO convertToUpdateDTO(DogBreed dogBreed) {
        return new DogBreedUpdateDTO(
                dogBreed.getDescription(),
                dogBreed.getLifeExpectancyMin(),
                dogBreed.getLifeExpectancyMax(),
                dogBreed.getMaleWeightMin(),
                dogBreed.getMaleWeightMax(),
                dogBreed.getFemaleWeightMin(),
                dogBreed.getFemaleWeightMax(),
                dogBreed.getHypoallergenic(),
                dogBreed.getSize()
        );
    }

    public static boolean checkForUpdates(DogBreed existingBreed, DogBreedExternalDTO newBreedDTO) {
        return !areAttributesEqual(existingBreed, newBreedDTO.getAttributeDTO());
    }

    private static boolean areAttributesEqual(DogBreed existingBreed, AttributesDTO newAttributes) {
        return existingBreed.getDescription().equals(newAttributes.getDescription()) &&
                existingBreed.getLifeExpectancyMin().equals(newAttributes.getLife().getMin()) &&
                existingBreed.getLifeExpectancyMax().equals(newAttributes.getLife().getMax()) &&
                existingBreed.getMaleWeightMin().equals(newAttributes.getMaleWeightDTO().getMin()) &&
                existingBreed.getMaleWeightMax().equals(newAttributes.getMaleWeightDTO().getMax()) &&
                existingBreed.getFemaleWeightMin().equals(newAttributes.getFemaleWeightDTO().getMin()) &&
                existingBreed.getFemaleWeightMax().equals(newAttributes.getFemaleWeightDTO().getMax()) &&
                existingBreed.getHypoallergenic().equals(newAttributes.getHypoallergenic());
    }

    public static DogBreed updateBreedFromExternalDTO(DogBreed existingBreed, DogBreedExternalDTO newBreedDTO) {
        AttributesDTO newAttributes = newBreedDTO.getAttributeDTO();
        existingBreed.setDescription(newAttributes.getDescription());
        existingBreed.setLifeExpectancyMin(newAttributes.getLife().getMin());
        existingBreed.setLifeExpectancyMax(newAttributes.getLife().getMax());
        existingBreed.setMaleWeightMin(newAttributes.getMaleWeightDTO().getMin());
        existingBreed.setMaleWeightMax(newAttributes.getMaleWeightDTO().getMax());
        existingBreed.setFemaleWeightMin(newAttributes.getFemaleWeightDTO().getMin());
        existingBreed.setFemaleWeightMax(newAttributes.getFemaleWeightDTO().getMax());
        existingBreed.setHypoallergenic(newAttributes.getHypoallergenic());

        return existingBreed;
    }
}