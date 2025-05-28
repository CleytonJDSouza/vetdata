package com.project.vetdata.service;

import com.project.vetdata.dto.PostOperativeCreateDTO;
import com.project.vetdata.mappers.MapperDTOToEntity;
import com.project.vetdata.model.PostOperative;
import com.project.vetdata.repository.PostOperativeRepository;
import com.project.vetdata.templates.PostOperativeTemplate;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PostOperativeServiceTest {
    @Mock
    private PostOperativeRepository repository;

    @Mock
    private MapperDTOToEntity<PostOperativeCreateDTO, PostOperative> mapper;

    @InjectMocks
    private PostOperativeService service;

    @Test
    public void given_valid_postOperativeCreateDTO_when_createPostOperative_is_called_then_postOperative_is_saved_and_returned(){
        PostOperativeCreateDTO createDTO = PostOperativeTemplate.getFakePostOperativeCreateDTO();
        PostOperative newPostOperative = PostOperativeTemplate.getFakePostOperative();

        when(mapper.map(any(PostOperativeCreateDTO.class))).thenReturn(newPostOperative);
        when(repository.save(any(PostOperative.class))).thenReturn(newPostOperative);

        PostOperative savedPostOperative = service.createPostOperative(createDTO);
        assertNotNull(savedPostOperative);

        verify(repository, times(1)).save(newPostOperative);
    }

    @Test
    public void given_valid_is_when_deletePostOperative_is_called_then_postOperative_is_deleted() {
        Long postOperativeID = 1L;

        service.deletePostOperative(postOperativeID);

        verify(repository, times(1)).deleteById(postOperativeID);
    }

    @Test
    public void given_postOperatives_exist_when_getAllPostOperatives_is_called_then_return_list_of_postOperatives() {
        List<PostOperative> mockPostOperative = List.of(PostOperativeTemplate.getFakePostOperative());

        when(repository.findAll()).thenReturn(mockPostOperative);
        List<PostOperative> result = service.getAllPostOperatives();

        assertNotNull(result, "A lista não pode ser nula.");
        assertEquals(1, result.size(), "A lista deve conter 1 pós operatório");
        verify(repository, times(1)).findAll();
    }

    @Test
    public void given_no_postOperative_when_getAllPostOperatives_is_called_then_return_empty_list() {
        when(repository.findAll()).thenReturn(Collections.emptyList());

        List<PostOperative> result = service.getAllPostOperatives();

        assertNotNull(result, "A lista não deve ser nula.");
        assertTrue(result.isEmpty(), "A lista deve estar vazia");
        verify(repository, times(1)).findAll();
    }

    @Test
    public void given_postOperative_exist_when_getAllPostOperativePaginated_is_called_then_return_page_of_postOperatives() {
        Pageable pageable = PageRequest.of(0,10);
        PostOperative postOperative = PostOperativeTemplate.getFakePostOperative();
        Page<PostOperative> postOperativePage = new PageImpl<>(List.of(postOperative));

        when(repository.findAll(pageable)).thenReturn(postOperativePage);

        Page<PostOperative> result = service.getAllPostOperativesPaginated(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(repository, times(1)).findAll(pageable);
    }

    @Test
    public void given_matching_postOperatives_when_getBySearchTerm_is_called_then_return_page_of_matching_postOperatives() {
        String term = "fisioterapia";
        Pageable pageable = PageRequest.of(0,10);
        PostOperative postOperative = PostOperativeTemplate.getFakePostOperative();
        Page<PostOperative> postOperativePage = new PageImpl<>(List.of(postOperative));

        when(repository.findByDescriptionContainingIgnoreCase(term, pageable)).thenReturn(postOperativePage);

        Page<PostOperative> result = service.getBySearchTerm(term, pageable);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.getTotalElements());
        verify(repository, times(1)).findByDescriptionContainingIgnoreCase(term, pageable);
    }

    @Test
    public void given_no_matching_postOperatives_when_getBySearchTerm_is_called_then_return_empty_page() {
        String term = "teste";
        Pageable pageable = PageRequest.of(0,10);
        Page<PostOperative> emptyPage = Page.empty(pageable);

        when(repository.findByDescriptionContainingIgnoreCase(term, pageable)).thenReturn(emptyPage);

        Page<PostOperative> result = service.getBySearchTerm(term, pageable);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(repository, times(1)).findByDescriptionContainingIgnoreCase(term, pageable);
    }
}
