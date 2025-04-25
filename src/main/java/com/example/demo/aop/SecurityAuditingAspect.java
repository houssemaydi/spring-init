package com.example.demo.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Aspect pour l'audit de sécurité des opérations sensibles
 * Journalise les accès aux fonctionnalités protégées
 */
@Aspect
@Component
@Slf4j
public class SecurityAuditingAspect {

    /**
     * Pointcut qui correspond aux méthodes protégées par @PreAuthorize
     */
    @Pointcut("@annotation(org.springframework.security.access.prepost.PreAuthorize)")
    public void securedMethodPointcut() {
        // Méthode vide pour le pointcut
    }

    /**
     * Pointcut qui correspond aux méthodes d'authentification
     */
    @Pointcut("execution(* com.example.demo.controller.AuthController.*(..))")
    public void authenticationMethodPointcut() {
        // Méthode vide pour le pointcut
    }

    /**
     * Enregistre les accès réussis aux méthodes sécurisées
     * @param joinPoint Point d'entrée dans le code où l'aspect est appliqué
     * @param result Le résultat retourné par la méthode
     */
    @AfterReturning(pointcut = "securedMethodPointcut()", returning = "result")
    public void logSecuredAccess(JoinPoint joinPoint, Object result) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication != null ? authentication.getName() : "anonymousUser";

        log.info("Accès autorisé: Utilisateur '{}' a exécuté {}.{}()",
            username,
            joinPoint.getSignature().getDeclaringTypeName(),
            joinPoint.getSignature().getName());
    }

    /**
     * Enregistre les tentatives d'authentification réussies
     * @param joinPoint Point d'entrée dans le code où l'aspect est appliqué
     * @param result Le résultat retourné par la méthode
     */
    @AfterReturning(pointcut = "execution(* com.example.demo.controller.AuthController.authenticateUser(..))", returning = "result")
    public void logSuccessfulAuthentication(JoinPoint joinPoint, Object result) {
        // Extrait le nom d'utilisateur du premier argument (LoginRequest)
        Object[] args = joinPoint.getArgs();
        if (args.length > 0 && args[0] != null) {
            try {
                // Tente d'extraire le nom d'utilisateur de LoginRequest
                String username = args[0].getClass().getMethod("getUsername").invoke(args[0]).toString();
                log.info("Authentification réussie pour l'utilisateur: '{}'", username);
            } catch (Exception e) {
                log.info("Authentification réussie");
            }
        }
    }

    /**
     * Enregistre les échecs d'authentification
     * @param joinPoint Point d'entrée dans le code où l'aspect est appliqué
     * @param exception L'exception levée
     */
    @AfterThrowing(pointcut = "execution(* com.example.demo.controller.AuthController.authenticateUser(..))", throwing = "exception")
    public void logFailedAuthentication(JoinPoint joinPoint, Throwable exception) {
        // Extrait le nom d'utilisateur du premier argument (LoginRequest)
        Object[] args = joinPoint.getArgs();
        if (args.length > 0 && args[0] != null) {
            try {
                // Tente d'extraire le nom d'utilisateur de LoginRequest
                String username = args[0].getClass().getMethod("getUsername").invoke(args[0]).toString();
                log.warn("Échec d'authentification pour l'utilisateur: '{}', raison: {}",
                    username, exception.getMessage());
            } catch (Exception e) {
                log.warn("Échec d'authentification, raison: {}", exception.getMessage());
            }
        }
    }

    /**
     * Enregistre les accès refusés aux méthodes sécurisées
     * @param joinPoint Point d'entrée dans le code où l'aspect est appliqué
     * @param exception L'exception levée
     */
    @AfterThrowing(pointcut = "securedMethodPointcut()", throwing = "exception")
    public void logSecurityException(JoinPoint joinPoint, Throwable exception) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication != null ? authentication.getName() : "anonymousUser";

        log.warn("Accès refusé: Utilisateur '{}' a essayé d'exécuter {}.{}(), exception: {}",
            username,
            joinPoint.getSignature().getDeclaringTypeName(),
            joinPoint.getSignature().getName(),
            exception.getMessage());
    }
}
