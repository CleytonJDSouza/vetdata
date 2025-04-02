package com.project.vetdata.service;

import com.project.vetdata.dto.UserCreateDTO;
import com.project.vetdata.model.User;

import java.util.Optional;

public interface UserService {
    User createUser(UserCreateDTO userCreateDTO);
    void deleteUser(Long id);
    Optional<User> getUserById(Long id);
}
