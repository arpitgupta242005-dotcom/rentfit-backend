package com.rentfit.service;

import com.rentfit.dto.LoginRequest;
import com.rentfit.dto.LoginResponse;
import com.rentfit.dto.RegisterRequest;
import com.rentfit.dto.RegisterResponse;

public interface AuthService {
    LoginResponse login(LoginRequest loginRequest);

    RegisterResponse register(RegisterRequest registerRequest);
}
