package com.project.vetdata.service;

import com.project.vetdata.dto.UserCreateDTO;
import com.project.vetdata.model.User;

public interface UserService {
    User createUser(UserCreateDTO userCreateDTO);
}
