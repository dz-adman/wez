package com.dz.auth.identity.service;

import com.dz.auth.identity.dao.repository.RefreshTokenRepository;
import com.dz.auth.identity.dao.repository.TokenRepository;
import com.dz.auth.identity.dao.stub.TokenType;
import com.dz.auth.identity.dto.AuthResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
public class LogoutService implements LogoutHandler {

    private final TokenRepository tokenRepo;
    private final RefreshTokenRepository refreshTokenRepo;

    @Override
    @Transactional
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) return;
        jwt = authHeader.substring(7);
        var storedToken = tokenRepo.findByToken(jwt);
        storedToken.ifPresent(token -> {
            refreshTokenRepo.delete(token.getRefreshToken());
            tokenRepo.delete(token);
        });
        SecurityContextHolder.clearContext();
        AuthResponse authResponse = AuthResponse.builder()
                .isAuthenticated(false)
                .build();
        response.setHeader("content-type", "application/json");
        try {
            new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
        } catch (IOException e) {
            log.warn("Unable to write AuthResponse to HttpServletResponse");
            log.warn(e.getMessage());
        }
    }
}
