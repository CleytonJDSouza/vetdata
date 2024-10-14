package com.project.vetdata.service;

import com.project.vetdata.model.DogBreed;
import com.project.vetdata.repository.DogBreedRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

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

    public void given_dogBreeds_pre_registered_when_a_pageable_is_null_then_should_return_an_empty_page() {

    }


    public DogBreed getFakeDogBreed() {
        return new DogBreed(null, "2", "Golden Retriever", "Amigável e inteligente e esperto",
                5, 10, 15D, 20D, 12.0, 18.0,
                false, "Medio");
    }

}
