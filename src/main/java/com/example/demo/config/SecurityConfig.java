package com.example.demo.config;

import com.example.demo.security.JwtAuthenticationFilter;
import com.example.demo.security.JwtAuthorizationFilter;
import com.example.demo.security.JwtUtils;
import com.example.demo.security.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuration principale de la sécurité de l'application
 * @EnableWebSecurity active la sécurité web de Spring Security
 * @EnableMethodSecurity active la sécurité au niveau des méthodes avec @PreAuthorize, etc.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthorizationFilter jwtAuthorizationFilter;

    /**
     * Configure l'encodeur de mot de passe
     * BCrypt est un algorithme de hachage sécurisé pour les mots de passe
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configure le gestionnaire d'authentification
     * Utilisé pour l'authentification des utilisateurs
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /**
     * Configure la chaîne de filtres de sécurité
     * Définit les règles d'accès aux différentes ressources
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           AuthenticationManager authenticationManager,
                                           JwtUtils jwtUtils) throws Exception {
        // Configurer le filtre d'authentification JWT avec le JwtUtils injecté
        JwtAuthenticationFilter jwtAuthenticationFilter =
            new JwtAuthenticationFilter(authenticationManager, jwtUtils);
        jwtAuthenticationFilter.setFilterProcessesUrl("/api/auth/login");

        http
            // Désactive CSRF car nous utilisons des jetons JWT
            .csrf(csrf -> csrf.disable())
            // Configure la gestion de session comme STATELESS (sans état)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // Configure les règles d'autorisation pour les requêtes HTTP
            .authorizeHttpRequests(auth -> auth
                // Endpoints publics accessibles sans authentification
                .requestMatchers(
                    "/api/auth/**",
                    "/h2-console/**",
                    "/error",
                    "/api/test/**", // Endpoints de test
                    // URLs Swagger et OpenAPI
                    "/swagger-ui.html",
                    "/swagger-ui/**",
                    "/v3/api-docs/**",
                    "/v3/api-docs.yaml",
                    "/swagger-resources/**",
                    "/webjars/**").permitAll()
                // Endpoints Actuator avec autorisations spécifiques
                .requestMatchers("/actuator").permitAll()
                .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                .requestMatchers("/actuator/prometheus").hasRole("ADMIN")
                .requestMatchers("/actuator/**").hasRole("ADMIN")
                // Tous les autres endpoints nécessitent une authentification
                .anyRequest().authenticated()
            )
            // Ajoute le filtre d'authentification JWT
            .addFilter(jwtAuthenticationFilter)
            // Ajoute le filtre d'autorisation JWT avant le filtre UsernamePasswordAuthenticationFilter
            .addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class);

        // Pour l'accès à la console H2 (uniquement en développement)
        http.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()));

        return http.build();
    }
}
