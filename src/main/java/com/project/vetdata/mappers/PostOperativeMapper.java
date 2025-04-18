package com.project.vetdata.mappers;

import com.project.vetdata.dto.DiagnosticCreateDTO;
import com.project.vetdata.dto.PostOperativeCreateDTO;
import com.project.vetdata.model.Diagnostic;
import com.project.vetdata.model.PostOperative;
import org.springframework.stereotype.Component;

@Component
public class PostOperativeMapper implements MapperDTOToEntity<PostOperativeCreateDTO, PostOperative>{

    @Override
    public PostOperative map(PostOperativeCreateDTO dto) {
        return new PostOperative(dto.getDescription());
    }
}
