package com.dz.auth.identity.service;

import com.dz.auth.identity.dto.AuthRequest;
import com.dz.auth.identity.dto.AuthResponse;
import com.dz.auth.identity.dto.RegistrationRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public interface AuthService {
    AuthResponse register(RegistrationRequest request);

    AuthResponse authenticate(AuthRequest request);

    void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException;

    AuthResponse authResponse();
}
