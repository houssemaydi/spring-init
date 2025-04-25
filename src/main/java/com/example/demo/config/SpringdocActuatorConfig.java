package com.example.demo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration Springdoc OpenAPI pour intégrer les endpoints Actuator
 */
@Configuration
public class SpringdocActuatorConfig {

    /**
     * Configure les informations globales de l'API OpenAPI
     * @return La configuration OpenAPI
     */
    @Bean
    public OpenAPI apiInfo() {
        return new OpenAPI()
            .info(new Info()
                .title("API Sécurisée avec Actuator")
                .description("API REST pour la gestion des utilisateurs, rôles et permissions avec monitoring via Actuator")
                .version("1.0.0")
                .license(new License()
                    .name("Apache 2.0")
                    .url("https://www.apache.org/licenses/LICENSE-2.0")));
    }
}
