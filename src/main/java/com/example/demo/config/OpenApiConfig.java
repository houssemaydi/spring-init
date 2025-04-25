package com.example.demo.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.security.SecuritySchemes;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration de l'API OpenAPI (Swagger)
 * Définit les informations générales de l'API et les schémas de sécurité
 */
@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "API de gestion de sécurité",
        version = "1.0",
        description = "API REST pour la gestion des utilisateurs, rôles et permissions",
        contact = @Contact(
            name = "Équipe de développement",
            email = "contact@example.com"
        ),
        license = @License(
            name = "Apache 2.0",
            url = "https://www.apache.org/licenses/LICENSE-2.0"
        )
    ),
    servers = {
        @Server(url = "/", description = "Serveur par défaut")
    },
    security = {
        @SecurityRequirement(name = "bearerAuth")
    }
)
@SecuritySchemes({
    @SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        description = "Entrez le token JWT avec le préfixe Bearer"
    )
})
public class OpenApiConfig {
    // La configuration est faite uniquement avec les annotations
}
