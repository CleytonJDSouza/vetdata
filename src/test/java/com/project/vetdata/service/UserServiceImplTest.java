package com.project.vetdata.service;

import com.project.vetdata.controller.UserRestController;
import com.project.vetdata.dto.UserCreateDTO;
import com.project.vetdata.model.User;
import com.project.vetdata.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.password4j.Password;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @InjectMocks
    private UserRestController userController;

    @Mock
    private UserRepository userRepository;

    @Test
    public void given_valid_userCreateDTO_when_createUser_is_called_then_user_is_saved_and_returned() {
        UserCreateDTO createDTO = getFakeUserCreateDTO();
        User newUser = getFakeUser();

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        when(userRepository.save(any(User.class))).thenReturn(newUser);

        User savedUser = userService.createUser(createDTO);

        verify(userRepository).save(userCaptor.capture());
        User capturedUser = userCaptor.getValue();

        assertNotNull(savedUser, "O usuário salvo não deve ser nulo");
        assertEquals(newUser.getName(), savedUser.getName(), "O nome do usuário deve ser igual");
        assertEquals(createDTO.getName(), capturedUser.getName(), "O nome do usuário deve ser mapeado corretamente");
        assertEquals(createDTO.getEmail(), capturedUser.getEmail(), "O email deve ser mapeado corretamente");
        assertNotNull(capturedUser.getPassword(), "A senha não deve ser nula");
        assertTrue(Password.check(createDTO.getPassword(), capturedUser.getPassword()).withBcrypt(), "A senha deve estar criptografada corretamente");
        assertNotNull(savedUser.getCreatedDate(), "A data de criação não deve ser nula");
        assertNotNull(savedUser.getPasswordLastUpdatedDate(), "A data da última atualização não deve ser nula");
    }

    @Test
    public void given_userCreateDTO_when_repository_fails_then_runtime_exception() {
        when(userRepository.save(any(User.class))).thenThrow(new RuntimeException("Erro ao salvar o usuário"));

        RuntimeException exception = assertThrows(RuntimeException.class, this::createUser);
        assertEquals("Erro ao salvar o usuário", exception.getMessage());
    }

    private void createUser() {
        userService.createUser(getFakeUserCreateDTO());
    }

    @Test
    public void given_valid_is_when_deleteUser_is_called_then_user_is_deleted() {
        Long userId = 1L;

        userService.deleteUser(userId);

        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    public void given_invalid_id_when_deleteUser_the_no_exception_is_thrown_and_repository_is_called() {
        Long invalidUserId = 999L;

        userService.deleteUser(invalidUserId);

        verify(userRepository, times(1)).deleteById(invalidUserId);
    }

    @Test
    public void given_users_exist_when_getAllUsers_is_called_then_return_list_of_users() {
        List<User> mockUser = List.of(getFakeUser(), getFakeUser2());

        when(userRepository.findAll()).thenReturn(mockUser);

        List<User> result = userService.getAllUsers();

        assertNotNull(result, "A lista não pode ser nula.");
        assertEquals(2, result.size(), "A lista deve conter 2 usuários");
        verify(userRepository, times(1)).findAll();
    }

    @Test
    public void given_no_users_when_getAllUsers_is_called_then_return_empty_list() {
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        List<User> result = userService.getAllUsers();

        assertNotNull(result, "A lista não deve ser nula.");
        assertTrue(result.isEmpty(), "A lista deve estar vazia");
        verify(userRepository, times(1)).findAll();
    }


    private UserCreateDTO getFakeUserCreateDTO() {
        UserCreateDTO dto = new UserCreateDTO();
        dto.setName("Beatriz");
        dto.setEmail("beatriz@vetdata.com");
        dto.setPassword("Senha%1");
        return dto;
    }

    private User getFakeUser() {
        User user = new User();
        user.setName("Beatriz");
        user.setEmail("beatriz@vetdata.com");

        String hashedPassword = Password.hash("Senha%1").withBcrypt().getResult();
        user.setPassword(hashedPassword);

        user.setCreatedDate(LocalDateTime.now());
        user.setPasswordLastUpdatedDate(LocalDateTime.now());
        return user;
    }

    private User getFakeUser2() {
        User user = new User();
        user.setName("Cleyton");
        user.setEmail("cleyton@vetdata.com");

        String hashedPassword = Password.hash("Senha%123").withBcrypt().getResult();
        user.setPassword(hashedPassword);

        user.setCreatedDate(LocalDateTime.now());
        user.setPasswordLastUpdatedDate(LocalDateTime.now());
        return user;
    }
}
