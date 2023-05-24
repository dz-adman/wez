package com.dz.auth.identity.config;

import com.dz.auth.identity.dao.repository.RefreshTokenRepository;
import com.dz.auth.identity.dao.repository.TokenRepository;
import com.dz.auth.identity.dao.stub.TokenType;
import com.dz.auth.identity.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final TokenRepository tokenRepo;
    private final RefreshTokenRepository refreshTokenRepo;

    private static final String[] WHITELIST = {
            "/register",
            "/login"
    };

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        jwt = authHeader.substring(7);
        userEmail = jwtService.extractUsername(jwt);
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
            // check the grant_type header for refresh-token
            String grantType = request.getHeader("grant_type");
            TokenType tokenType = grantType == null ? TokenType.BEARER_JWT : grantType.equals("refresh_token") ? TokenType.BEARER_REFRESH : null;
            if (tokenType != null) {
                var now = Instant.now();
                boolean isTokenValid = switch (tokenType) {
                    case BEARER_JWT -> tokenRepo.findByToken(jwt)
                            .map(t -> !t.getValidFrom().isAfter(now) && !t.getValidTill().isBefore(now))
                            .orElse(false);
                    case BEARER_REFRESH -> refreshTokenRepo.findByToken(jwt)
                            .map(t -> !t.getValidFrom().isAfter(now) && !t.getValidTill().isBefore(now))
                            .orElse(false);
                };
                if (jwtService.isTokenValid(jwt, tokenType, userDetails) && isTokenValid) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        }
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return Arrays.stream(WHITELIST).anyMatch(uriPart -> request.getServletPath().contains(uriPart));
    }
}
