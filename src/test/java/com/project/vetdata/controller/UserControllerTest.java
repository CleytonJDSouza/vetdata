package com.project.vetdata.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.password4j.Password;
import com.project.vetdata.dto.UserCreateDTO;
import com.project.vetdata.model.User;
import com.project.vetdata.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@ActiveProfiles("test")
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserController userController;

    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    public void given_valid_user_when_createUser_then_returns_createdUser() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        UserCreateDTO userCreateDTO = new UserCreateDTO("Beatriz", "beatriz@vetdata.com", "Senha%1");

        User user = new User(1L, "Beatriz", "beatriz@vetdata.com",
                Password.hash("Senha%1").withBcrypt().getResult(), now, now);

        when(userService.createUser(any(UserCreateDTO.class))).thenReturn(user);

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userCreateDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(user.getName()))
                .andExpect(jsonPath("$.email").value(user.getEmail()))
                .andExpect(jsonPath("$.password").isNotEmpty())
                .andExpect(jsonPath("$.createdDate").isNotEmpty())
                .andExpect(jsonPath("$.passwordLastUpdatedDate").isNotEmpty());
    }

    @Test
    public void given_invalidUser_when_createUser_then_returnsBadRequest() throws Exception {
        UserCreateDTO userCreateDTO = new UserCreateDTO();
        userCreateDTO.setName(null);
        userCreateDTO.setEmail("email_invalido");
        userCreateDTO.setPassword("123");

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userCreateDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").value("Campo Obrigatório"))
                .andExpect(jsonPath("$.email").value("Formato de email inválido"))
                .andExpect(jsonPath("$.password").value("A senha deve conter pelo menos uma letra maiúscula, um número, um caractere especial (%&*) e ter no mínimo de 5 caracteres."));
    }

    @Test
    public void given_existing_userId_when_deleteUser_then_returns_noContent() throws Exception {
        Long userId = 1L;
        User user = new User(userId, "Beatriz", "beatiz@vetdata.com", "Senha%1", LocalDateTime.now(), LocalDateTime.now());

        when(userService.getUserById(userId)).thenReturn(Optional.of(user));

        mockMvc.perform(MockMvcRequestBuilders.delete("/users/{id}", userId))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).deleteUser(userId);
    }

    @Test
    public void given_user_does_not_exist_when_deleteUser_then_returns_notFound() throws Exception {
        Long invalidUserId = 999L;

        when(userService.getUserById(invalidUserId)).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.delete("/users/{id}", invalidUserId))
                .andExpect(status().isNotFound());
    }

    @Test
    public void given_users_exist_when_getAllUser_then_returns_list_of_users() throws Exception {
        List<User> users = List.of(
                new User(1L, "Beatriz", "beatriz@vetdata.com", "Senha%1", LocalDateTime.now(), LocalDateTime.now()),
                new User(2L, "Cleyton", "cleyton@vetdata.com", "Senha%123", LocalDateTime.now(), LocalDateTime.now()));

        when(userService.getAllUsers()).thenReturn(users);

        mockMvc.perform(MockMvcRequestBuilders.get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].name").value("Beatriz"))
                .andExpect(jsonPath("$[0].email").value("beatriz@vetdata.com"))
                .andExpect(jsonPath("$[1].name").value("Cleyton"))
                .andExpect(jsonPath("$[1].email").value("cleyton@vetdata.com"));
    }

    @Test
    public void given_no_users_when_getAllUsers_then_returns_empty_list() throws Exception {
        when(userService.getAllUsers()).thenReturn(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders.get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(0));
    }
}
