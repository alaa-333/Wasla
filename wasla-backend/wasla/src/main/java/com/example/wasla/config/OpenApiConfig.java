package com.example.wasla.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI / Swagger configuration per docs Section 5.2.
 * Adds JWT Bearer auth scheme to Swagger UI.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI waslaOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Wasla API")
                        .description("Logistics marketplace backend — connecting clients with truck drivers")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Wasla Team")
                                .email("support@wasla.app")))
                .addSecurityItem(new SecurityRequirement().addList("Bearer JWT"))
                .components(new Components()
                        .addSecuritySchemes("Bearer JWT",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Paste your access token here")));
    }
}
