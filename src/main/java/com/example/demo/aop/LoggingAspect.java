package com.example.demo.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Aspect pour la journalisation des méthodes dans toute l'application
 * Utilise Spring AOP pour intercepter les appels de méthodes
 */
@Aspect
@Component
@Slf4j
public class LoggingAspect {

    /**
     * Pointcut qui correspond à tous les repositories, services et contrôleurs REST
     */
    @Pointcut("within(@org.springframework.stereotype.Repository *)" +
        " || within(@org.springframework.stereotype.Service *)" +
        " || within(@org.springframework.web.bind.annotation.RestController *)")
    public void springBeanPointcut() {
        // Méthode vide pour le pointcut
    }

    /**
     * Pointcut qui correspond à toutes les classes dans le package com.example.demo
     */
    @Pointcut("within(com.example.demo..*)")
    public void applicationPackagePointcut() {
        // Méthode vide pour le pointcut
    }

    /**
     * Aspect autour des méthodes pour logger le temps d'exécution
     * @param joinPoint Point d'entrée dans le code où l'aspect est appliqué
     * @return Le résultat de la méthode interceptée
     * @throws Throwable Si une exception se produit dans la méthode
     */
    @Around("applicationPackagePointcut() && springBeanPointcut()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        if (log.isDebugEnabled()) {
            log.debug("Entrée: {}.{}() avec arguments = {}", joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(), Arrays.toString(joinPoint.getArgs()));
        }
        try {
            long start = System.currentTimeMillis();
            Object result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - start;

            if (log.isDebugEnabled()) {
                log.debug("Sortie: {}.{}() avec résultat = {}", joinPoint.getSignature().getDeclaringTypeName(),
                    joinPoint.getSignature().getName(), result);
            }

            if (executionTime > 500) {  // Considérer les méthodes qui prennent plus de 500ms comme lentes
                log.warn("Méthode lente: {}.{}() a pris {}ms", joinPoint.getSignature().getDeclaringTypeName(),
                    joinPoint.getSignature().getName(), executionTime);
            } else {
                log.debug("Temps d'exécution: {}.{}() a pris {}ms", joinPoint.getSignature().getDeclaringTypeName(),
                    joinPoint.getSignature().getName(), executionTime);
            }

            return result;
        } catch (IllegalArgumentException e) {
            log.error("Argument illégal: {} dans {}.{}()", Arrays.toString(joinPoint.getArgs()),
                joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName());
            throw e;
        }
    }

    /**
     * Aspect après une exception pour logger les erreurs
     * @param joinPoint Point d'entrée dans le code où l'aspect est appliqué
     * @param e L'exception levée
     */
    @AfterThrowing(pointcut = "applicationPackagePointcut() && springBeanPointcut()", throwing = "e")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable e) {
        log.error("Exception dans {}.{}() avec cause = {}", joinPoint.getSignature().getDeclaringTypeName(),
            joinPoint.getSignature().getName(), e.getCause() != null ? e.getCause() : "NULL");
        if (log.isDebugEnabled()) {
            log.debug("Exception complète: ", e);
        }
    }
}
