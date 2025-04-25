package com.example.demo.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

/**
 * Aspect pour gérer l'invalidation automatique du cache
 * Nettoie les caches appropriés lorsque les données sont modifiées
 */
@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class CacheInvalidationAspect {

    private final CacheManager cacheManager;

    /**
     * Pointcut qui correspond aux méthodes qui créent des utilisateurs
     */
    @Pointcut("execution(* com.example.demo.repository.UserRepository.save(..)) || " +
        "execution(* com.example.demo.controller.AuthController.registerUser(..))")
    public void userModificationPointcut() {
        // Méthode vide pour le pointcut
    }

    /**
     * Pointcut qui correspond aux méthodes qui suppriment des utilisateurs
     */
    @Pointcut("execution(* com.example.demo.repository.UserRepository.delete*(..)) || " +
        "execution(* com.example.demo.controller.UserController.deleteUser(..))")
    public void userDeletionPointcut() {
        // Méthode vide pour le pointcut
    }

    /**
     * Pointcut qui correspond aux méthodes qui modifient des rôles
     */
    @Pointcut("execution(* com.example.demo.repository.RoleRepository.save(..)) || " +
        "execution(* com.example.demo.repository.RoleRepository.delete*(..))")
    public void roleModificationPointcut() {
        // Méthode vide pour le pointcut
    }

    /**
     * Invalide les caches liés aux utilisateurs après modification
     * @param joinPoint Point d'entrée dans le code où l'aspect est appliqué
     * @param result Le résultat de la méthode
     */
    @AfterReturning(pointcut = "userModificationPointcut()", returning = "result")
    public void invalidateUserCaches(JoinPoint joinPoint, Object result) {
        log.debug("Invalidation des caches d'utilisateurs après opération: {}",
            joinPoint.getSignature().getName());

        invalidateCache("users");
        invalidateCache("userByUsername");
        invalidateCache("userByEmail");
    }

    /**
     * Invalide les caches liés aux utilisateurs après suppression
     * @param joinPoint Point d'entrée dans le code où l'aspect est appliqué
     */
    @AfterReturning(pointcut = "userDeletionPointcut()")
    public void invalidateUserCachesAfterDeletion(JoinPoint joinPoint) {
        log.debug("Invalidation des caches d'utilisateurs après suppression");

        invalidateCache("users");
        invalidateCache("userByUsername");
        invalidateCache("userByEmail");
    }

    /**
     * Invalide les caches liés aux rôles après modification
     * @param joinPoint Point d'entrée dans le code où l'aspect est appliqué
     */
    @AfterReturning(pointcut = "roleModificationPointcut()")
    public void invalidateRoleCaches(JoinPoint joinPoint) {
        log.debug("Invalidation des caches de rôles après opération: {}",
            joinPoint.getSignature().getName());

        invalidateCache("roles");
        // Comme les rôles sont liés aux utilisateurs, on invalide aussi leurs caches
        invalidateCache("users");
        invalidateCache("userByUsername");
    }

    /**
     * Méthode utilitaire pour invalider un cache spécifique
     * @param cacheName Le nom du cache à invalider
     */
    private void invalidateCache(String cacheName) {
        try {
            Cache cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                cache.clear();
                log.debug("Cache '{}' invalidé avec succès", cacheName);
            } else {
                log.debug("Cache '{}' non trouvé, aucune invalidation nécessaire", cacheName);
            }
        } catch (Exception e) {
            log.warn("Erreur lors de l'invalidation du cache '{}': {}", cacheName, e.getMessage());
        }
    }
}
