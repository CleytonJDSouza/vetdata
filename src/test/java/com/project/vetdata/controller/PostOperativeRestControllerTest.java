package com.project.vetdata.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.vetdata.model.PostOperative;
import com.project.vetdata.service.PostOperativeService;
import com.project.vetdata.templates.PostOperativeTemplate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PostOperativeRestController.class)
public class PostOperativeRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PostOperativeService postOperativeService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void given_search_term_when_getAllPostOperatives_paginated_then_call_getBySearchTerm() throws Exception {
        String searchTerm = "fisio";
        Pageable paging = PageRequest.of(0, 10, Sort.by("id"));
        PostOperative post1 = PostOperativeTemplate.getFakePostOperative();
        PostOperative post2 = PostOperativeTemplate.getFakePostOperative2();
        Page<PostOperative> page = new PageImpl<>(List.of(post1, post2), paging, 2);

        when(postOperativeService.getBySearchTerm(eq(searchTerm), eq(paging))).thenReturn(page);

        mockMvc.perform(MockMvcRequestBuilders.get("/post-operatives/search")
                        .param("searchByTerm", searchTerm)
                        .param("page", "0")
                        .param("qtdRecordsPage", "10")
                        .param("sortBy", "id")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.total").value(2))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.qtdRecordsPage").value(10));

        verify(postOperativeService).getBySearchTerm(eq(searchTerm), eq(paging));
    }

    @Test
    public void given_no_search_term_when_getAllPostOperatives_paginated_then_call_getAllPostOperatives_paginated() throws Exception {
        Pageable paging = PageRequest.of(0, 10, Sort.by("id"));
        PostOperative post = PostOperativeTemplate.getFakePostOperative();
        Page<PostOperative> page = new PageImpl<>(List.of(post), paging, 1);

        when(postOperativeService.getAllPostOperativesPaginated(eq(paging))).thenReturn(page);

        mockMvc.perform(MockMvcRequestBuilders.get("/post-operatives/search")
                        .param("page", "0")
                        .param("qtdRecordsPage", "10")
                        .param("sortBy", "id")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.total").value(1))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.qtdRecordsPage").value(10));

        verify(postOperativeService).getAllPostOperativesPaginated(eq(paging));
    }

    @Test
    public void given_emptyPage_when_getAllPostOperatives_paginated_then_return_noContent() throws Exception {
        Pageable paging = PageRequest.of(0, 10, Sort.by("id"));
        Page<PostOperative> emptyPage = Page.empty();

        when(postOperativeService.getAllPostOperativesPaginated(eq(paging))).thenReturn(emptyPage);

        mockMvc.perform(MockMvcRequestBuilders.get("/post-operatives/search")
                        .param("page", "0")
                        .param("qtdRecordsPage", "10")
                        .param("sortBy", "id")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(postOperativeService).getAllPostOperativesPaginated(eq(paging));
    }
}

