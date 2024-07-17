package com.project.vetdata.controller;

import com.project.vetdata.model.DogBreed;
import com.project.vetdata.service.DogBreedService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/breeds")
public class DogBreedController {

    private final DogBreedService dogBreedService;

    @Autowired
    public DogBreedController(DogBreedService dogBreedService) {
        this.dogBreedService = dogBreedService;
    }

    @GetMapping
    public ResponseEntity<List<DogBreed>> getAllDogBreeds() {
        return ResponseEntity.ok(dogBreedService.getAllDogBreeds());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DogBreed> getDogBreedById(@PathVariable Long id) {
        return dogBreedService.getDogBreedId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<DogBreed> createDogBreed(@Valid @RequestBody DogBreed dogBreed) {
        DogBreed createdDogBreed = dogBreedService.createDogBreed(dogBreed);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdDogBreed);
    }
}