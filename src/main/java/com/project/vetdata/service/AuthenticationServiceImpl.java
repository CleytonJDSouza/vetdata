package com.project.vetdata.service;

import com.password4j.Password;
import com.project.vetdata.dto.AuthenticationRequestDTO;
import com.project.vetdata.dto.AuthenticationResponseDTO;
import com.project.vetdata.enums.AuthenticationStatus;
import com.project.vetdata.model.User;
import com.project.vetdata.repository.UserRepository;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;

    public AuthenticationServiceImpl(AuthenticationManager authenticationManager, UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
    }

    @Override
    public AuthenticationResponseDTO authenticate(AuthenticationRequestDTO dto) {
        try {
            User user = userRepository.findByEmail(dto.email())
                    .orElseThrow(() -> new BadCredentialsException("Usuário não encontrado"));

            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(dto.email(), dto.password());

            authenticationManager.authenticate(authToken);

            return new AuthenticationResponseDTO(AuthenticationStatus.AUTHORIZED, user.getName());

        } catch (BadCredentialsException e) {
            return new AuthenticationResponseDTO(AuthenticationStatus.NOT_AUTHORIZED, null);
        } catch (DisabledException e) {
            return new AuthenticationResponseDTO(AuthenticationStatus.INACTIVE, null);
        } catch (CredentialsExpiredException e) {
            return new AuthenticationResponseDTO(AuthenticationStatus.PASSWORD_EXPIRED, null);
        }
    }
}
