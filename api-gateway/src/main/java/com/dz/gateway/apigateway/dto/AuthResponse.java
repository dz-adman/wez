package com.dz.gateway.apigateway.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.Set;

@Builder
public record AuthResponse(
        long userId, boolean isAuthenticated, Role role, Set<Permission> permissions,
        @JsonProperty("access_token") String accessToken,
        @JsonProperty("refresh_token") String refreshToken
) {
}
