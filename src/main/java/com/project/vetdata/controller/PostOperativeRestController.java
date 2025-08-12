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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/post-operatives")
public class PostOperativeRestController {
    private static final Logger logger = LoggerFactory.getLogger(PostOperativeRestController.class);
    private final PostOperativeService service;

    public PostOperativeRestController(PostOperativeService service) {
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
                .orElseGet(() -> {
                    logger.warn("Pós-operatório com ID {} não encontrado!", id);
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                });
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

    @Operation(summary = "Buscar todos os pós-operatórios (com paginação e busca)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pós-operatório(s) encontrado(s)!",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = PostOperative.class))}),
            @ApiResponse(responseCode = "204", description = "Nenhum pós-operatório encontrado!", content = @Content)
    })
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> getAllPostOperativesPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int qtdRecordsPage,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(required = false) String searchByTerm
    ) {

        Pageable pageable = PageRequest.of(page, qtdRecordsPage, Sort.by(sortBy));
        Page<PostOperative> postOperativesPage = StringUtils.hasText(searchByTerm)
                ? service.getBySearchTerm(searchByTerm, pageable)
                : service.getAllPostOperativesPaginated(pageable);

        if (postOperativesPage.isEmpty()) {
            logger.info("Nenhum resultado encontrado para a busca paginada.");
            return ResponseEntity.noContent().build();
        }

        logger.info("Busca paginada retornou {} registros", postOperativesPage.getTotalElements());

        Map<String, Object> response = Map.of(
                "total", postOperativesPage.getTotalElements(),
                "qtdRecordsPage", postOperativesPage.getSize(),
                "page", postOperativesPage.getNumber(),
                "data", postOperativesPage.getContent()
        );

        return ResponseEntity.ok(response);
    }
}
