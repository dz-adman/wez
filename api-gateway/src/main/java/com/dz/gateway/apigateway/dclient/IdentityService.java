package com.dz.gateway.apigateway.dclient;

import com.dz.gateway.apigateway.dto.AuthResponse;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.service.annotation.GetExchange;
import reactor.core.publisher.Mono;

public interface IdentityService {
    @GetExchange("/auth/validate-token")
    @LoadBalanced
    Mono<AuthResponse> validateToken(@RequestHeader(HttpHeaders.AUTHORIZATION) String bearerToken);
}
