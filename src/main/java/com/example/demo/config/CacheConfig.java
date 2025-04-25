package com.example.demo.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

/**
 * Configuration du système de cache
 * Permet de stocker temporairement des données pour améliorer les performances
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * Configure le gestionnaire de cache
     * @return Le gestionnaire de cache configuré
     */
    @Bean
    public CacheManager cacheManager() {
        ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager();
        cacheManager.setCacheNames(Arrays.asList(
            "users",           // Cache pour la liste des utilisateurs
            "userByUsername",  // Cache pour les recherches par nom d'utilisateur
            "userByEmail",     // Cache pour les recherches par email
            "roles",           // Cache pour la liste des rôles
            "permissions"      // Cache pour la liste des permissions
        ));
        return cacheManager;
    }
}
