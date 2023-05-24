package com.dz.auth.identity.dto;

import com.dz.auth.identity.dao.stub.Role;
import lombok.Builder;

@Builder
public record RegistrationRequest(String loginId, String firstName, String lastName, String email, String password, Role role) {
}
