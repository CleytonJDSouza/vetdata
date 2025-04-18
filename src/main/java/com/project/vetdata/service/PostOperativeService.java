package com.project.vetdata.service;

import com.project.vetdata.dto.DiagnosticCreateDTO;
import com.project.vetdata.dto.PostOperativeCreateDTO;
import com.project.vetdata.mappers.MapperDTOToEntity;
import com.project.vetdata.model.Diagnostic;
import com.project.vetdata.model.PostOperative;
import com.project.vetdata.repository.DiagnosticRepository;
import com.project.vetdata.repository.PostOperativeRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class PostOperativeService {
    private final PostOperativeRepository repository;
    private final MapperDTOToEntity<PostOperativeCreateDTO, PostOperative> mapper;

    public PostOperativeService(PostOperativeRepository repository,
                                @Qualifier("postOperativeMapper") MapperDTOToEntity<PostOperativeCreateDTO, PostOperative> mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public PostOperative createPostOperative(PostOperativeCreateDTO dto) {
        return repository.save(mapper.map(dto));
    }

    public void deletePostOperative(Long id) {
        repository.deleteById(id);
    }

    public Optional<PostOperative> getPostOperativeById(Long id) {
        return repository.findById(id);
    }

    public List<PostOperative> getAllPostOperatives() {
        return repository.findAll();
    }
}