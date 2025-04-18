package com.project.vetdata.controller;

import com.project.vetdata.dto.PostOperativeCreateDTO;
import com.project.vetdata.model.PostOperative;
import com.project.vetdata.service.PostOperativeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/post-operatives")
public class PostOperativeController {
    private final PostOperativeService service;

    public PostOperativeController(PostOperativeService service) {
        this.service = service;
    }

    @Operation(summary = "Criar um novo pós-operatório")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Pós operatório criado com sucesso!",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = PostOperative.class))}),
            @ApiResponse(responseCode = "400", description = "Dados inválidos!", content = @Content)
    })
    @PostMapping
    public ResponseEntity<PostOperative> createPostOperative(@Valid @RequestBody PostOperativeCreateDTO dto) {
        PostOperative createdPostOperative = service.createPostOperative(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPostOperative);
    }

    @Operation(summary = "Remover pós-operatório por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Pós operatório removido!"),
            @ApiResponse(responseCode = "404", description = "Pós operatório não encontrado!", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePostOperative(@PathVariable Long id) {
        return service.getPostOperativeById(id)
                .map(this::handleDelete)
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    private ResponseEntity<Void> handleDelete(PostOperative postOperative)  {
        service.deletePostOperative(postOperative.getId());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "Listar todos os pós-operatórios")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de pós-operatórios retornada com sucesso!",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = PostOperative.class))})
    })
    @GetMapping
    public ResponseEntity<List<PostOperative>> getAllPostOperatives() {
        List<PostOperative> postOperatives = service.getAllPostOperatives();
        return ResponseEntity.ok(postOperatives);
    }
}
