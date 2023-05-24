package com.dz.auth.identity.dto;

import com.dz.auth.identity.dao.stub.Permission;
import com.dz.auth.identity.dao.stub.Role;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.List;
import java.util.Set;

@Builder
public record AuthResponse(
        long userId, boolean isAuthenticated, Role role, Set<Permission> permissions,
        @JsonProperty("access_token") String accessToken,
        @JsonProperty("refresh_token") String refreshToken
) {
}
