package com.steven.auth.services;

import com.steven.auth.dto.LoginRequest;
import com.steven.auth.dto.TokenResponse;

public interface AuthService {

    TokenResponse autenticar(LoginRequest request) throws Exception;
}
