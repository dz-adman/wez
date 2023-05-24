package com.dz.auth.identity.service.impl;

import com.dz.auth.identity.dao.entity.LoginDetails;
import com.dz.auth.identity.dao.entity.RefreshToken;
import com.dz.auth.identity.dao.repository.LoginDetailsRepository;
import com.dz.auth.identity.dao.repository.RefreshTokenRepository;
import com.dz.auth.identity.dto.AuthRequest;
import com.dz.auth.identity.dto.AuthResponse;
import com.dz.auth.identity.dto.RegistrationRequest;
import com.dz.auth.identity.dao.entity.Token;
import com.dz.auth.identity.dao.entity.User;
import com.dz.auth.identity.dao.repository.TokenRepository;
import com.dz.auth.identity.dao.repository.UserRepository;
import com.dz.auth.identity.dao.stub.TokenType;
import com.dz.auth.identity.exception.UserAlreadyExistsException;
import com.dz.auth.identity.service.AuthService;
import com.dz.auth.identity.service.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final LoginDetailsRepository loginDetailsRepo;
    private final UserRepository userRepo;
    private final TokenRepository tokenRepo;
    private final RefreshTokenRepository refreshTokenRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;
    @Value("${application.security.jwt.refresh-token.expiration}")
    private long refreshExpiration;

    @Override
    public AuthResponse register(RegistrationRequest request) {
        Optional<LoginDetails> optionalLoginDetails = loginDetailsRepo.findByLoginId(request.loginId());
        if (optionalLoginDetails.isPresent()) throw new UserAlreadyExistsException("User Already Present with these details");
        var user = User.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .build();
        userRepo.save(user);
        LoginDetails loginDetails = LoginDetails.builder()
                .user(user)
                .loginId(request.loginId())
                .password(passwordEncoder.encode(request.password()))
                .role(request.role())
                .enabled(true)
                .locked(false)
                .deleted(false)
                .build();
        loginDetailsRepo.save(loginDetails);
        return AuthResponse.builder()
                .userId(user.getId())
                .isAuthenticated(true)
                .role(loginDetails.getRole())
                .permissions(loginDetails.getRole().getPermissions())
                .build();
    }

    @Override
    @Transactional
    public AuthResponse authenticate(AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.username(), request.password()));
        LoginDetails loginDetails = (LoginDetails) authentication.getPrincipal();
        var jwtToken = jwtService.generateToken(loginDetails);
        var refreshToken = jwtService.generateRefreshToken(loginDetails);
        RefreshToken rt = saveRefreshToken(loginDetails.getUser(), refreshToken);
        saveJwtToken(loginDetails.getUser(), jwtToken, rt);
        return AuthResponse.builder()
                .userId(loginDetails.getUser().getId())
                .isAuthenticated(true)
                .role(loginDetails.getRole())
                .permissions(loginDetails.getRole().getPermissions())
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    private void saveJwtToken(User user, String jwtToken, RefreshToken refreshToken) {
        var now = Instant.now();
        var jwt = Token.builder()
                .user(user)
                .token(jwtToken)
                .refreshToken(refreshToken)
                .validFrom(now)
                .validTill(now.plusMillis(jwtExpiration))
                .build();
        tokenRepo.save(jwt);
    }

    private RefreshToken saveRefreshToken(User user, String refreshToken) {
        var now = Instant.now();
        var refresh = RefreshToken.builder()
                .user(user)
                .token(refreshToken)
                .validFrom(now)
                .validTill(now.plusMillis(refreshExpiration))
                .build();
        return refreshTokenRepo.save(refresh);
    }

    @Override
    @Transactional
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }
        final String refreshToken = authHeader.substring(7);
        final String username = jwtService.extractUsername(refreshToken);
        if (username != null) {
            var loginDetails = loginDetailsRepo.findByLoginId(username).orElseThrow();
            if (jwtService.isTokenValid(refreshToken, TokenType.BEARER_REFRESH, loginDetails)) {
                var accessToken = jwtService.generateToken(loginDetails);
                RefreshToken rt = refreshTokenRepo.findByToken(refreshToken).orElse(null);
                deleteAllJwtTokensForUser(loginDetails.getUser(), rt);
                saveJwtToken(loginDetails.getUser(), accessToken, rt);
                var authResponse = AuthResponse.builder()
                        .userId(loginDetails.getUser().getId())
                        .isAuthenticated(true)
                        .role(loginDetails.getRole())
                        .permissions(loginDetails.getRole().getPermissions())
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();
                response.setHeader("content-type", "application/json");
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
        }
    }

    @Override
    @Transactional
    public AuthResponse authResponse() {
        LoginDetails loginDetails = (LoginDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return AuthResponse.builder()
                .userId(loginDetails.getUser().getId())
                .isAuthenticated(true)
                .role(loginDetails.getRole())
                .permissions(loginDetails.getRole().getPermissions())
                .build();
    }

    private void deleteAllJwtTokensForUser(User user, RefreshToken refreshToken) {
        var userTokens = tokenRepo.findAllByUserAndRefreshToken(user, refreshToken);
        if (userTokens.isEmpty()) return;
        userTokens.forEach(tokenRepo::delete);
    }

}
