package com.dz.app.wordez.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(info());
    }

    private Info info() {
        return new Info()
                .contact(contact())
                .description("Word EZ")
                .title("WordEZ  Service")
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
}
