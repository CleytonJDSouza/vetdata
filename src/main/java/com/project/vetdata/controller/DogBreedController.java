package com.project.vetdata.controller;

import com.project.vetdata.dto.DogBreedCreateDTO;
import com.project.vetdata.dto.DogBreedDTO;
import com.project.vetdata.dto.DogBreedUpdateDTO;
import com.project.vetdata.model.DogBreed;
import com.project.vetdata.service.DogBreedService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/breeds")
public class DogBreedController {

    private final DogBreedService dogBreedService;

    @Autowired
    public DogBreedController(DogBreedService dogBreedService) {
        this.dogBreedService = dogBreedService;
    }

    @Operation(summary = "Buscar todas as raças")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Raça encontrada!",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = DogBreed.class)) }),
            @ApiResponse(responseCode = "204", description = "Nenhuma raça encontrada!", content = @Content)
    })
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllDogBreeds(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int qtdRecordsPage,
            @RequestParam(defaultValue = "id") String sortBy
    ) {
        Pageable paging = PageRequest.of(page, qtdRecordsPage, Sort.by(sortBy));
        Page<DogBreed> pageBreeds = dogBreedService.getAllDogBreeds(paging);

        if (pageBreeds.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        Map<String, Object> response = new HashMap<>();
        response.put("total", pageBreeds.getTotalElements());
        response.put("qtdRescordsPage", pageBreeds.getSize());
        response.put("page", pageBreeds.getNumber());
        response.put("data", pageBreeds.getContent());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Buscar Raça pelo Id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Raça Encontrada!",
                    content = { @Content(mediaType = "application/json",
                    schema = @Schema(implementation = DogBreedDTO.class)) }),
            @ApiResponse(responseCode = "400", description = "Id Inválido!", content = @Content),
            @ApiResponse(responseCode = "404", description = "Raça Não Encontrada!", content = @Content) })
    @GetMapping("/{id}")
    public ResponseEntity<?> getDogBreedById(@PathVariable Long id) {
        return dogBreedService.getDogBreedId(id)
                .map(dogBreed -> ResponseEntity.ok(convertToDto(dogBreed)))
                .orElse(ResponseEntity.notFound().build());
    }

    private DogBreedDTO convertToDto(DogBreed dogBreed) {
        return new DogBreedDTO(
                dogBreed.getId(),
                dogBreed.getName(),
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

    @Operation(summary = "Criar uma nova raça")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Raça criada com sucesso!",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = DogBreed.class)) }),
            @ApiResponse(responseCode = "400", description = "Dados inválidos!", content = @Content)
    })
    @PostMapping
    public ResponseEntity<DogBreed> createDogBreed(@Valid @RequestBody DogBreedCreateDTO dogBreedCreateDTO) {
        DogBreed createdDogBreed = dogBreedService.createDogBreed(dogBreedCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdDogBreed);
    }

    @Operation(summary = "Remover raça por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Raça removida!"),
            @ApiResponse(responseCode = "404", description = "Raça não encontrada!", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDogBreed(@PathVariable Long id) {
        return dogBreedService.getDogBreedId(id)
                .map(dogBreed -> {dogBreedService.deleteDogBreed(id);
                    return new ResponseEntity<Void>(HttpStatus.NO_CONTENT); })
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @Operation(summary = "Atualizar uma raça existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Raça Atualizada!",
            content = { @Content(mediaType = "application/json",
                        schema = @Schema(implementation = DogBreed.class))}),
            @ApiResponse(responseCode = "400", description = "Dados inválidos!", content = @Content),
            @ApiResponse(responseCode = "404", description = "Raça não encontrada!", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<DogBreed> updateDogBreed(@PathVariable Long id, @Valid @RequestBody DogBreedUpdateDTO dogBreedUpdateDTO) {
        DogBreed updateDogBreed = dogBreedService.updateDogBreed(id, dogBreedUpdateDTO);
        return ResponseEntity.ok(updateDogBreed);
    }
}