package com.example.demo.actuator;

import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.Selector;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Endpoint personnalisé d'Actuator pour les informations de sécurité
 * Accessible via /actuator/securityInfo
 */
@Component
@Endpoint(id = "securityInfo")
@RequiredArgsConstructor
public class SecurityInfoEndpoint {

    private final UserRepository userRepository;

    /**
     * Récupère les informations générales de sécurité
     * @return Les statistiques de sécurité de l'application
     */
    @ReadOperation
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Map<String, Object> securityInfo() {
        Map<String, Object> details = new HashMap<>();

        details.put("timestamp", Instant.now().toString());
        details.put("totalUsers", userRepository.count());
        details.put("activeUsers", userRepository.findAll().stream()
            .filter(user -> user.isEnabled() && user.isAccountNonLocked())
            .count());

        details.put("systemInfo", getSystemInfo());

        return details;
    }

    /**
     * Récupère les informations détaillées sur un utilisateur spécifique
     * @param username Le nom d'utilisateur à rechercher
     * @return Les détails de l'utilisateur
     */
    @ReadOperation
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Map<String, Object> userInfo(@Selector String username) {
        Map<String, Object> details = new HashMap<>();

        userRepository.findByUsername(username).ifPresentOrElse(
            user -> {
                details.put("found", true);
                details.put("username", user.getUsername());
                details.put("email", user.getEmail());
                details.put("enabled", user.isEnabled());
                details.put("accountNonLocked", user.isAccountNonLocked());
                details.put("accountNonExpired", user.isAccountNonExpired());
                details.put("credentialsNonExpired", user.isCredentialsNonExpired());
                details.put("roles", user.getRoles().stream()
                    .map(role -> role.getName())
                    .toList());
            },
            () -> {
                details.put("found", false);
                details.put("message", "Utilisateur non trouvé: " + username);
            }
        );

        return details;
    }

    /**
     * Récupère les informations système
     * @return Les informations système
     */
    private Map<String, Object> getSystemInfo() {
        Map<String, Object> systemInfo = new HashMap<>();

        systemInfo.put("javaVersion", System.getProperty("java.version"));
        systemInfo.put("javaVendor", System.getProperty("java.vendor"));
        systemInfo.put("osName", System.getProperty("os.name"));
        systemInfo.put("osVersion", System.getProperty("os.version"));
        systemInfo.put("osArch", System.getProperty("os.arch"));
        systemInfo.put("availableProcessors", Runtime.getRuntime().availableProcessors());
        systemInfo.put("totalMemory", Runtime.getRuntime().totalMemory());
        systemInfo.put("freeMemory", Runtime.getRuntime().freeMemory());
        systemInfo.put("maxMemory", Runtime.getRuntime().maxMemory());

        return systemInfo;
    }
}
