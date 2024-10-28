package com.project.vetdata.service;

import com.project.vetdata.dto.DogBreedCreateDTO;
import com.project.vetdata.dto.DogBreedUpdateDTO;
import com.project.vetdata.exception.BreedNotFoundException;
import com.project.vetdata.model.DogBreed;
import com.project.vetdata.repository.DogBreedRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DogBreedServiceImplTest {

    @Mock
    private DogBreedRepository dogBreedRepository;

    @InjectMocks
    private DogBreedServiceImpl dogBreedServiceImpl;

    @Test
    public void given_dogBreeds_pre_registered_when_a_page_is_informed_then_the_breeds_are_returned() {
        Pageable pageable = PageRequest.of(0,10);
        DogBreed dogBreed = getFakeDogBreed();

        Page<DogBreed> page = new PageImpl<>(Collections.singletonList(dogBreed));
        when(dogBreedServiceImpl.getAllDogBreeds(pageable))
                .thenReturn(page);

        Page<DogBreed> breeds = dogBreedServiceImpl.getAllDogBreeds(pageable);
        assertNotNull(breeds);
        assertTrue(breeds.getSize()>0, "Quantidade deve ser maior que 0!");
    }

    @Test
    public void given_dogBreeds_pre_registered_when_a_pageable_is_null_then_should_return_an_empty_page() {
        Pageable pageable = null;
        Page<DogBreed> emptyPage = Page.empty();

        when(dogBreedRepository.findAll(pageable)).thenReturn(emptyPage);
        Page<DogBreed> result = dogBreedServiceImpl.getAllDogBreeds(pageable);

        assertNotNull(result);
        assertEquals(0, result.getTotalElements(), "A página deve estar vazia");
    }

    @Test
    public  void given_existing_dogBreedId_when_getDogBreedId_is_called_then_dogBreed_is_returned() {
        Long existingId = 1L;
        DogBreed dogBreed = getFakeDogBreed();
        Optional<DogBreed> optionalDogBreed = Optional.of(dogBreed);

        when(dogBreedRepository.findById(existingId)).thenReturn(optionalDogBreed);
        Optional<DogBreed> result = dogBreedServiceImpl.getDogBreedId(existingId);

        assertTrue(result.isPresent(), "A raça de cahorro deve ser retornada");
        assertEquals(dogBreed.getName(), result.get().getName(), "O nome da raça deve ser igual");
    }

    @Test
    public  void given_non_existing_dogBreedId_when_getDogBreedId_id_called_then_optional_empty_is_returned() {
        Long nonExistingId = 99L;
        Optional<DogBreed> optionalEmpty = Optional.empty();

        when(dogBreedRepository.findById(nonExistingId)).thenReturn(optionalEmpty);
        Optional<DogBreed> result = dogBreedServiceImpl.getDogBreedId(nonExistingId);

        assertFalse(result.isPresent(), "A raça de cachorro não deve ser encontrada");
    }

    @Test
    public void given_valid_dogBreedCreateDTO_when_createDogBreed_is_called_then_dogBreed_is_saved_and_returned() {
        DogBreedCreateDTO createDTO = getFakeDogBreedCreateDTO();
        DogBreed newDogBreed = getFakeDogBreed();
        newDogBreed.setId(1L);

        ArgumentCaptor<DogBreed> dogBreedCaptor = ArgumentCaptor.forClass(DogBreed.class);
        when(dogBreedRepository.save(any(DogBreed.class))).thenReturn(newDogBreed);
        DogBreed savedBreed = dogBreedServiceImpl.createDogBreed(createDTO);

        verify(dogBreedRepository).save(dogBreedCaptor.capture());
        DogBreed capturedDogBreed = dogBreedCaptor.getValue();

        assertNotNull(savedBreed, "A raça de cachorro não deve ser nula");
        assertEquals(newDogBreed.getName(), savedBreed.getName(), "O nome da raça deve ser igual");
        assertEquals(newDogBreed.getId(), savedBreed.getId(), "O ID da raça deve ser gerado corretamente");
        assertEquals(createDTO.getName(), capturedDogBreed.getName(), "O nome da raça deve ser mapeado corretamente");
    }

    @Test
    public void given_dogBreedCreateDTO_when_repository_fails_then_throw_runtime_exception() {
        DogBreedCreateDTO createDTO = getFakeDogBreedCreateDTO();
        when(dogBreedRepository.save(any(DogBreed.class))).thenThrow(new RuntimeException("Erro ao salvar a raça"));
        RuntimeException exception = assertThrows(RuntimeException.class, this::createDogBreed);
        assertEquals("Erro ao salvar a raça", exception.getMessage());
    }

    private void createDogBreed() {
        dogBreedServiceImpl.createDogBreed(getFakeDogBreedCreateDTO());
    }

    @Test
    public void given_valid_id_when_deleteDogBreed_is_called_then_dogBreed_is_deleted() {
        Long dogBreedId = 1L;
        dogBreedServiceImpl.deleteDogBreed(dogBreedId);
        verify(dogBreedRepository, times(1)).deleteById(dogBreedId);
    }

    @Test
    public void given_valid_idExternalApi_when_findByExternalApi_is_called_then_dogBreed_is_returned() {
        String idExternalApi = "12345";
        DogBreed expectedDogBreed = getFakeDogBreed();

        when(dogBreedRepository.findByIdExternalApi(idExternalApi)).thenReturn(Optional.of(expectedDogBreed));
        Optional<DogBreed> returnedDogBreeds = dogBreedServiceImpl.findByExternalApi(idExternalApi);

        verify(dogBreedRepository, times(1)).findByIdExternalApi(idExternalApi);
        assertTrue(returnedDogBreeds.isPresent(), "A raça de cachorro não deve ser nula");
        assertEquals(expectedDogBreed, returnedDogBreeds.get(), "As raças de cachorro retornadas devem ser iguais ás esperadas");

    }

    @Test
    public void given_valid_id_dogBreedUpdateDTO_when_updateDogBreed_is_called_then_dogBreed_is_update_and_saved() {
        Long id = 1L;
        DogBreed existingBreed = getFakeDogBreed();
        DogBreedUpdateDTO updateDTO = getFakeDogBreedUpdateDTO();

        when(dogBreedRepository.findById(id)).thenReturn(Optional.of(existingBreed));
        when(dogBreedRepository.save(any(DogBreed.class))).thenReturn(existingBreed);

        DogBreed updatedBreed = dogBreedServiceImpl.updateDogBreed(id, updateDTO);

        verify(dogBreedRepository, times(1)).findById(id);
        verify(dogBreedRepository, times(1)).save(existingBreed);

        assertEquals(updatedBreed.getDescription(), updatedBreed.getDescription(), "A descrição deve ser atualizada corretamente");
        assertEquals(updatedBreed.getLifeExpectancyMin(), updatedBreed.getLifeExpectancyMin(), "A expectativa de vida mínima deve ser atualizada");
        assertEquals(updatedBreed.getLifeExpectancyMax(), updatedBreed.getLifeExpectancyMax(), "A expectativa de vida máxima deve ser atualizada");
        assertEquals(updatedBreed.getMaleWeightMin(), updatedBreed.getMaleWeightMin(), "O peso mínimo dos machos deve ser atualizado");
        assertEquals(updatedBreed.getMaleWeightMax(), updatedBreed.getMaleWeightMax(), "O peso máximo dos machos deve ser atualizado");
        assertEquals(updatedBreed.getFemaleWeightMin(), updatedBreed.getFemaleWeightMin(), "O peso mínimo das femeas deve ser atualizado");
        assertEquals(updatedBreed.getFemaleWeightMax(), updatedBreed.getFemaleWeightMax(), "O peso máximo das femeas deve ser atualizado");
        assertEquals(updatedBreed.getHypoallergenic(), updatedBreed.getHypoallergenic(), "A propriedade hipoalergênica deve ser atualizada");
        assertEquals(updatedBreed.getSize(), updatedBreed.getSize(), "O tamanho deve ser atualizado");
    }

    @Test
    public void given_invalid_id_when_updateDogBreed_is_called_then_breedNotFoundException_is_thrown() {
        Long invalidId = 99L;
        DogBreedUpdateDTO updateDTO = getFakeDogBreedUpdateDTO();

        when(dogBreedRepository.findById(invalidId)).thenReturn(Optional.empty());

        assertThrows(BreedNotFoundException.class, this::callUpdateDogBreed, "Deve lançar NotFoundException quando a raça não é encontrada");

        verify(dogBreedRepository, times(1)).findById(invalidId);
        verify(dogBreedRepository, never()).save(any(DogBreed.class));
    }

    private void callUpdateDogBreed() {
        dogBreedServiceImpl.updateDogBreed(99L, getFakeDogBreedUpdateDTO());
    }


    @Test
    public void given_valid_updateDTO_with_all_fields_when_updateDogBreed_then_all_fields_are_updated() {
        Long id = 1L;
        DogBreed existingBreed = getFakeDogBreed();
        DogBreedUpdateDTO updateDTO = getFakeDogBreedUpdateDTO();

        when(dogBreedRepository.findById(id)).thenReturn(Optional.of(existingBreed));
        when(dogBreedRepository.save(any(DogBreed.class))).thenReturn(existingBreed);

        DogBreed updatedBreed = dogBreedServiceImpl.updateDogBreed(id, updateDTO);

        verify(dogBreedRepository, times(1)).findById(id);
        verify(dogBreedRepository, times(1)).save(existingBreed);

        assertEquals(updateDTO.getDescription(), updatedBreed.getDescription(), "A descrição deve ser atualizada");
        assertEquals(updateDTO.getLifeExpectancyMin(), updatedBreed.getLifeExpectancyMin(), "A expectativa de vida mínima deve ser atualizada");
        assertEquals(updateDTO.getLifeExpectancyMax(), updatedBreed.getLifeExpectancyMax(), "A expectativa de vida máxima deve ser atualizada");
        assertEquals(updateDTO.getMaleWeightMin(), updatedBreed.getMaleWeightMin(), "O peso mínimo dos machos deve ser atualizado");
        assertEquals(updateDTO.getMaleWeightMax(), updatedBreed.getMaleWeightMax(), "O peso máximo dos machos deve ser atualizado");
        assertEquals(updateDTO.getFemaleWeightMin(), updatedBreed.getFemaleWeightMin(), "O peso mínimo das fêmeas deve ser atualizado");
        assertEquals(updateDTO.getFemaleWeightMax(), updatedBreed.getFemaleWeightMax(), "O peso máximo das fêmeas deve ser atualizado");
        assertEquals(updateDTO.getHypoallergenic(), updatedBreed.getHypoallergenic(), "A propriedade hipoalergênica deve ser atualizada");
        assertEquals(updateDTO.getSize(), updatedBreed.getSize(), "O tamanho deve ser atualizado");
    }

    @Test
    public void given_updateDTO_with_null_fields_when_updateDogBreed_then_existing_values_are_retained() {
        Long id = 1L;
        DogBreed existingBreed = getFakeDogBreed();
        DogBreedUpdateDTO updateDTO = new DogBreedUpdateDTO();

        when(dogBreedRepository.findById(id)).thenReturn(Optional.of(existingBreed));
        when(dogBreedRepository.save(any(DogBreed.class))).thenReturn(existingBreed);

        DogBreed updatedBreed = dogBreedServiceImpl.updateDogBreed(id, updateDTO);

        verify(dogBreedRepository, times(1)).findById(id);
        verify(dogBreedRepository, times(1)).save(existingBreed);

        assertEquals(existingBreed.getDescription(), updatedBreed.getDescription(), "A descrição existente deve ser mantida");
        assertEquals(existingBreed.getLifeExpectancyMin(), updatedBreed.getLifeExpectancyMin(), "A expectativa de vida mínima existente deve ser mantida");
        assertEquals(existingBreed.getLifeExpectancyMax(), updatedBreed.getLifeExpectancyMax(), "A expectativa de vida máxima existente deve ser mantida");
        assertEquals(existingBreed.getMaleWeightMin(), updatedBreed.getMaleWeightMin(), "O peso mínimo dos machos existente deve ser mantido");
        assertEquals(existingBreed.getMaleWeightMax(), updatedBreed.getMaleWeightMax(), "O peso máximo dos machos existente deve ser mantido");
        assertEquals(existingBreed.getFemaleWeightMin(), updatedBreed.getFemaleWeightMin(), "O peso mínimo das fêmeas existente deve ser mantido");
        assertEquals(existingBreed.getFemaleWeightMax(), updatedBreed.getFemaleWeightMax(), "O peso máximo das fêmeas existente deve ser mantido");
        assertEquals(existingBreed.getHypoallergenic(), updatedBreed.getHypoallergenic(), "A propriedade hipoalergênica existente deve ser mantida");
        assertEquals(existingBreed.getSize(), updatedBreed.getSize(), "O tamanho existente deve ser mantido");
    }

    public DogBreed getFakeDogBreed() {
        return new DogBreed(1L, "2", "Golden Retriever", "Amigável e inteligente e esperto",
                10, 12, 30D, 34D, 25D, 29D,
                false, "Meéio");
    }

    public DogBreedCreateDTO getFakeDogBreedCreateDTO() {
        DogBreedCreateDTO dto = new DogBreedCreateDTO();
        dto.setName("Golden Retriever");
        dto.setDescription("Amigável e inteligente");
        dto.setLifeExpectancyMin(10);
        dto.setLifeExpectancyMax(12);
        dto.setMaleWeightMin(30D);
        dto.setMaleWeightMax(34D);
        dto.setFemaleWeightMin(25D);
        dto.setFemaleWeightMax(29D);
        dto.setHypoallergenic(false);
        dto.setSize("Médio");
        return dto;
    }

    public DogBreedUpdateDTO getFakeDogBreedUpdateDTO() {
        DogBreedUpdateDTO updateDTO = new DogBreedUpdateDTO();
        updateDTO.setDescription("Amigável, inteligente e companheiro");
        updateDTO.setLifeExpectancyMin(11);
        updateDTO.setLifeExpectancyMax(13);
        updateDTO.setMaleWeightMin(31D);
        updateDTO.setMaleWeightMax(35D);
        updateDTO.setFemaleWeightMin(26D);
        updateDTO.setFemaleWeightMax(29D);
        updateDTO.setHypoallergenic(true);
        updateDTO.setSize("Médio");
        return updateDTO;
    }
}
