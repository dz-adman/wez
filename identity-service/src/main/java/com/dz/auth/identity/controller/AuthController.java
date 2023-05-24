package com.dz.auth.identity.controller;

import com.dz.auth.identity.dto.AuthRequest;
import com.dz.auth.identity.dto.AuthResponse;
import com.dz.auth.identity.dto.RegistrationRequest;
import com.dz.auth.identity.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@Tag(name = "AUTH", description = "AUTH related APIs")
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @Operation(
            summary = "User Registration",
            description = "This API provides functionality for new user registration to the application. This API does not require authentication.",
            tags = "REGISTRATION"
    )
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegistrationRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @Operation(
            summary = "Get Auth Response",
            description = "This API can be used to Authenticate User and get the AuthResponse that includes tokens and other details",
            tags = "AUTHENTICATION"
    )
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> authenticate(@RequestBody AuthRequest authRequest) {
        return ResponseEntity.ok(authService.authenticate(authRequest));
    }

    @Operation(
            summary = "Refresh Auth-Token",
            description = "This API can be used to Refresh the Auth-Token",
            tags = "AUTHENTICATION"
    )
    @GetMapping("/refresh-token")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        authService.refreshToken(request, response);
    }

    @Operation(
            summary = "Validate Auth-Token",
            description = "This API can be used to Validate the Auth-Token",
            tags = "AUTHENTICATION"
    )
    @GetMapping("/validate-token")
    public ResponseEntity<AuthResponse> validateToken() {
        return ResponseEntity.ok(authService.authResponse());
    }

}
