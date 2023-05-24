package com.dz.gateway.apigateway.config;

import com.dz.gateway.apigateway.config.filters.AuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class GatewayConfig {

    private final AuthFilter authFilter;

    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder rlb) {
        return rlb.routes()
                .route("identity-service", r -> r.path("/ids/**")
                        .filters(filterSpec -> filterSpec
                                .prefixPath("/identity")
                                .rewritePath("/ids", "/auth")
                        )
                        .uri("lb://identity-service")
                )
                .route("wordez-service", r -> r.path("/wqez/**")
                        .filters(filterSpec -> filterSpec
                                .filter(authFilter)
                                .rewritePath("/wqez", "/wez")
                        )
                        .uri("lb://wordez-service")
                )
                .build();
    }
}
