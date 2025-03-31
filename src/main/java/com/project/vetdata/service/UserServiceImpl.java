package com.project.vetdata.service;

import com.password4j.Password;
import com.project.vetdata.dto.UserCreateDTO;
import com.project.vetdata.model.User;
import com.project.vetdata.repository.UserRepository;
import org.springframework.stereotype.Service;


@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User createUser(UserCreateDTO userCreateDTO) {
        User user = fromCreateDTO(userCreateDTO);
        return userRepository.save(user);
    }

    private User fromCreateDTO(UserCreateDTO dto) {
        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());

        String hashedPassword = Password.hash(dto.getPassword()).withBcrypt().getResult();
        user.setPassword(hashedPassword);

        return user;
    }
}