package com.dz.gateway.apigateway.config;

import com.dz.gateway.apigateway.dclient.IdentityService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class AppConfig {
    @Bean
    public IdentityService identityServiceDClient(WebClient.Builder builder) {
        HttpServiceProxyFactory httpServiceProxyFactory = HttpServiceProxyFactory
                .builder(WebClientAdapter.forClient(builder.baseUrl("http://localhost:9000/identity").build()))
                .build();
        return httpServiceProxyFactory.createClient(IdentityService.class);
    }
}
