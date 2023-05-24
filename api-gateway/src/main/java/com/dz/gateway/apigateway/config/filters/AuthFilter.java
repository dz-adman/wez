package com.dz.gateway.apigateway.config.filters;

import com.dz.gateway.apigateway.dclient.IdentityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.Ordered;
import org.springframework.http.*;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Component
@Slf4j
@RequiredArgsConstructor
public class AuthFilter implements GatewayFilter, Ordered {

    private final IdentityService identityService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            log.warn("Authorization Header Missing!!");
            return exchange.getResponse().setComplete();
        }
        String authHeader = Objects.requireNonNull(exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION)).get(0);
        String[] parts = authHeader.split(" ");
        if (parts.length != 2 || !"Bearer".equals(parts[0])) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            log.warn("Invalid Authorization Header!!");
            return exchange.getResponse().setComplete();
        }

        return identityService.validateToken(authHeader)
                .flatMap(authResponse -> {
                    if (authResponse.isAuthenticated()) {
                        log.info("Auth Success");
                        ServerHttpRequest mutateRequest = exchange.getRequest().mutate()
                                .header("x-user-id", String.valueOf(authResponse.userId()))
                                .build();
                        ServerWebExchange mutateServerWebExchange = exchange.mutate().request(mutateRequest).build();
                        return chain.filter(mutateServerWebExchange);
                    } else {
                        log.warn("Auth Failed!");
                        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                        return exchange.getResponse().setComplete();
                    }
                }).then();
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
