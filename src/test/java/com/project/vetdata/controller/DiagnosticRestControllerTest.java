package com.project.vetdata.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.vetdata.configs.TestSecurityConfig;
import com.project.vetdata.model.Diagnostic;
import com.project.vetdata.service.DiagnosticService;
import com.project.vetdata.templates.DiagnosticTemplate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
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
import org.springframework.security.test.context.support.WithMockUser;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DiagnosticRestController.class)
@Import(TestSecurityConfig.class)
public class DiagnosticRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DiagnosticService diagnosticService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void given_search_term_when_getAllDiagnostics_paginated_then_call_getBySearchTerm() throws Exception {
        String searchTerm = "gri";
        Pageable paging = PageRequest.of(0, 10, Sort.by("id"));
        Diagnostic diagnostic1 = DiagnosticTemplate.getFakeDiagnostic();
        Diagnostic diagnostic2 = DiagnosticTemplate.getFakeDiagnostic2();
        Page<Diagnostic> page = new PageImpl<>(List.of(diagnostic1, diagnostic2), paging, 2);

        when(diagnosticService.getBySearchTerm(eq(searchTerm), eq(paging))).thenReturn(page);

        mockMvc.perform(MockMvcRequestBuilders.get("/diagnostics/search")
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

        verify(diagnosticService).getBySearchTerm(eq(searchTerm), eq(paging));
    }

    @Test
    public void given_no_search_term_when_getAllDiagnostics_paginated_then_call_getAllDiagnostics_paginated() throws Exception {
        Pageable paging = PageRequest.of(0, 10, Sort.by("id"));
        Diagnostic diagnostic = DiagnosticTemplate.getFakeDiagnostic2();
        Page<Diagnostic> page = new PageImpl<>(List.of(diagnostic), paging, 1);

        when(diagnosticService.getAllDiagnosticsPaginated(eq(paging))).thenReturn(page);

        mockMvc.perform(MockMvcRequestBuilders.get("/diagnostics/search")
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

        verify(diagnosticService).getAllDiagnosticsPaginated(eq(paging));
    }

    @Test
    public void given_emptyPage_when_getAllDiagnostics_paginated_then_return_noContent() throws Exception {
        Pageable paging = PageRequest.of(0, 10, Sort.by("id"));
        Page<Diagnostic> emptyPage = Page.empty();

        when(diagnosticService.getAllDiagnosticsPaginated(eq(paging))).thenReturn(emptyPage);

        mockMvc.perform(MockMvcRequestBuilders.get("/diagnostics/search")
                        .param("page", "0")
                        .param("qtdRecordsPage", "10")
                        .param("sortBy", "id")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(diagnosticService).getAllDiagnosticsPaginated(eq(paging));
    }
}
