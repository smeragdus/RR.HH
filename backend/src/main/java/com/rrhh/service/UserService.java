package com.rrhh.service;

import com.rrhh.dto.request.CreateUserRequest;
import com.rrhh.dto.response.UserResponse;
import com.rrhh.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    Page<UserResponse> getAllUsers(Pageable pageable);
    UserResponse getUserById(Long id);
    UserResponse createUser(CreateUserRequest request);
    UserResponse updateUser(Long id, CreateUserRequest request);
    void deactivateUser(Long id);
    User getCurrentUser(String username);
}
