package com.dz.auth.identity.service;

import com.dz.auth.identity.dao.stub.TokenType;
import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.function.Function;

@Service
public interface JwtService {
    String extractUsername(String token);

    <T> T extractClaim(String token, Function<Claims, T> claimsResolver);

    String generateToken(UserDetails userDetails);

    String generateToken(Map<String, Object> extraClaims, UserDetails userDetails);

    String generateRefreshToken(UserDetails userDetails);

    String buildToken(Map<String, Object> extraClaims, UserDetails userDetails, long expiration);

    boolean isTokenValid(String token, TokenType tokenType, UserDetails userDetails);
}
