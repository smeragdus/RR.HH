package com.rrhh.service;

import com.rrhh.dto.request.LoginRequest;
import com.rrhh.dto.request.ChangePasswordRequest;
import com.rrhh.dto.response.JwtResponse;

public interface AuthService {
    JwtResponse login(LoginRequest request);
    void changePassword(Long userId, ChangePasswordRequest request);
}
