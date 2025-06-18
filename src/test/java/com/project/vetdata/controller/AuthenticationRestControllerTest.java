package com.project.vetdata.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.vetdata.configs.TestSecurityConfig;
import com.project.vetdata.dto.AuthenticationRequestDTO;
import com.project.vetdata.dto.AuthenticationResponseDTO;
import com.project.vetdata.enums.AuthenticationStatus;
import com.project.vetdata.service.AuthenticationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthenticationController.class)
@Import(TestSecurityConfig.class)
public class AuthenticationRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthenticationService authenticationService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void given_valid_credentials_when_login_then_returns_authorized() throws Exception {
        AuthenticationRequestDTO requestDTO = new AuthenticationRequestDTO("aline@vetdata.com", "Senha%1");
        AuthenticationResponseDTO responseDTO = new AuthenticationResponseDTO(AuthenticationStatus.AUTHORIZED, "Aline");

        when(authenticationService.authenticate(eq(requestDTO)))
                .thenReturn(responseDTO);

        mockMvc.perform(MockMvcRequestBuilders.post("/authentications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("AUTHORIZED"))
                .andExpect(jsonPath("$.name").value("Aline"));

        verify(authenticationService).authenticate(eq(requestDTO));
    }

    @Test
    public void given_invalid_email_when_login_then_returns_not_authorized() throws Exception {
        AuthenticationRequestDTO requestDTO = new AuthenticationRequestDTO("andrea@vetdata.com", "Senha%1");
        AuthenticationResponseDTO responseDTO = new AuthenticationResponseDTO(AuthenticationStatus.NOT_AUTHORIZED, null);

        when(authenticationService.authenticate(eq(requestDTO)))
                .thenReturn(responseDTO);

        mockMvc.perform(MockMvcRequestBuilders.post("/authentications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("NOT_AUTHORIZED"))
                .andExpect(jsonPath("$.name").doesNotExist());

        verify(authenticationService).authenticate(eq(requestDTO));

    }

    @Test
    public void given_invalid_password_when_login_then_returns_not_authorized() throws Exception {
        AuthenticationRequestDTO requestDTO = new AuthenticationRequestDTO("aline@vetdata.com", "Senha%2");
        AuthenticationResponseDTO responseDTO = new AuthenticationResponseDTO(AuthenticationStatus.NOT_AUTHORIZED, null);

        when(authenticationService.authenticate(eq(requestDTO)))
                .thenReturn(responseDTO);

        mockMvc.perform(MockMvcRequestBuilders.post("/authentications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("NOT_AUTHORIZED"))
                .andExpect(jsonPath("$.name").doesNotExist());

        verify(authenticationService).authenticate(eq(requestDTO));
    }

    @Test
    public void given_blank_fields_when_login_then_returns_badRequest() throws Exception {
        AuthenticationRequestDTO requestDTO = new AuthenticationRequestDTO("", "");

        mockMvc.perform(MockMvcRequestBuilders.post("/authentications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest());
    }
}
