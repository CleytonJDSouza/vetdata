package com.project.vetdata.controller;

import com.project.vetdata.model.DogBreed;
import com.project.vetdata.service.DogBreedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/breeds")
public class DogBreedController {

    private final DogBreedService dogBreedService;

    @Autowired
    public DogBreedController(DogBreedService dogBreedService) {
        this.dogBreedService = dogBreedService;
    }

    @PostMapping
    public ResponseEntity<DogBreed> createDogBreed(@RequestBody DogBreed dogBreed) {
        DogBreed newDogBreed = dogBreedService.createDogBreed(dogBreed);
        return new ResponseEntity<>(newDogBreed, HttpStatus.CREATED);
    }
}
