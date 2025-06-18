package com.project.vetdata.service;

import com.password4j.Password;
import com.project.vetdata.dto.AuthenticationRequestDTO;
import com.project.vetdata.dto.AuthenticationResponseDTO;
import com.project.vetdata.enums.AuthenticationStatus;
import com.project.vetdata.repository.UserRepository;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;

    public AuthenticationServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public AuthenticationResponseDTO authenticate(AuthenticationRequestDTO dto) {
        return userRepository.findByEmail(dto.email())
                .map(user -> {
                    boolean passwordOk = Password.check(dto.password(), user.getPassword()).withBcrypt();

                    if (passwordOk) {
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                user.getEmail(),
                                null,
                                List.of()
                        );
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        return new AuthenticationResponseDTO(AuthenticationStatus.AUTHORIZED, user.getName());
                    } else {
                        return new AuthenticationResponseDTO(AuthenticationStatus.NOT_AUTHORIZED, null);
                    }
                })
                .orElseGet(() -> new AuthenticationResponseDTO(AuthenticationStatus.NOT_AUTHORIZED, null));
    }

}
