package com.project.vetdata.service;

import com.password4j.Password;
import com.project.vetdata.dto.AuthenticationRequestDTO;
import com.project.vetdata.dto.AuthenticationResponseDTO;
import com.project.vetdata.enums.AuthenticationStatus;
import com.project.vetdata.model.User;
import com.project.vetdata.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceImplTest {

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    @Mock
    private UserRepository userRepository;

    @Test
    public void given_valid_credentials_when_authenticate_then_return_authorized() {
        String email = "cleyton@vetdata.com";
        String password = "Senha%1";
        String hashedPassword = Password.hash(password).withBcrypt().getResult();

        User user = new User();
        user.setName("Cleyton");
        user.setEmail(email);
        user.setPassword(hashedPassword);
        user.setCreatedDate(LocalDateTime.now());
        user.setPasswordLastUpdatedDate(LocalDateTime.now());

        AuthenticationRequestDTO dto = new AuthenticationRequestDTO(email, password);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        AuthenticationResponseDTO response = authenticationService.authenticate(dto);

        assertNotNull(response);
        assertEquals(AuthenticationStatus.AUTHORIZED, response.status());
        assertEquals("Cleyton", response.name());
    }

    @Test
    public void given_invalid_password_when_authenticate_then_return_not_authorized() {
        String email = "cleyton@vetdata.com";
        String password = "Senha%1";
        String wrongPassword = "Senha%2";
        String hashedPassWord = Password.hash(password).withBcrypt().getResult();

        User user = new User();
        user.setName("Cleyton");
        user.setEmail(email);
        user.setPassword(hashedPassWord);
        user.setCreatedDate(LocalDateTime.now());
        user.setPasswordLastUpdatedDate(LocalDateTime.now());

        AuthenticationRequestDTO dto = new AuthenticationRequestDTO(email, wrongPassword);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        AuthenticationResponseDTO response = authenticationService.authenticate(dto);

        assertNotNull(response);
        assertEquals(AuthenticationStatus.NOT_AUTHORIZED, response.status());
        assertNull(response.name());
    }

    @Test
    public void given_none_existent_email_when_authenticate_then_return_not_authorized() {
        String email = "beatriz@vetdata.com";
        String password = "Senha%1";

        AuthenticationRequestDTO dto = new AuthenticationRequestDTO(email, password);

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        AuthenticationResponseDTO response = authenticationService.authenticate(dto);

        assertNotNull(response);
        assertEquals(AuthenticationStatus.NOT_AUTHORIZED, response.status());
        assertNull(response.name());
    }
}
