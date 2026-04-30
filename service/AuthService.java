package com.jobportal.service;

import com.jobportal.dto.JwtResponse;
import com.jobportal.dto.LoginRequest;
import com.jobportal.dto.RegisterRequest;

public interface AuthService {
    JwtResponse authenticateUser(LoginRequest loginRequest);
    void registerUser(RegisterRequest registerRequest);
}
