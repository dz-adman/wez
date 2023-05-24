package com.dz.auth.identity.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(info())
                .addSecurityItem(new SecurityRequirement().addList("BearerAuth"))
                .components(new Components().addSecuritySchemes("BearerAuth", securityScheme()));
    }

    private Info info() {
        return new Info()
                .contact(contact())
                .description("Identity & Auth Management")
                .title("Identity Service")
                .version("1.0")
                .license(null)
                .termsOfService(null);
    }

    private Contact contact() {
        return new Contact()
                .name("Arun Kumar")
                .email("arun.akdhiman@gmail.com")
                .url("https://www.linkedin.com/in/dkumararun/");
    }

    private SecurityScheme securityScheme() {
        return new SecurityScheme()
                .name("BearerAuth")
                .description("JWT Auth")
                .scheme("bearer")
                .type(SecurityScheme.Type.HTTP)
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER);
    }
}
