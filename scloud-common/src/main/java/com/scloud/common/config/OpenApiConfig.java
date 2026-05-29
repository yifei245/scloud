package com.scloud.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    private static final String BEARER_AUTH = "BearerAuth";

    @Bean
    public OpenAPI openAPI(@Value("${spring.application.name:scloud}") String appName,
            @Value("${scloud.openapi.server-url:/}") String serverUrl) {
        return new OpenAPI()
                .info(new Info().title(appName + " API").version("1.0.0"))
                .servers(List.of(new Server().url(serverUrl)))
                .components(new Components().addSecuritySchemes(BEARER_AUTH, new SecurityScheme()
                        .name("Authorization")
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")))
                .security(List.of(new SecurityRequirement().addList(BEARER_AUTH)));
    }
}
