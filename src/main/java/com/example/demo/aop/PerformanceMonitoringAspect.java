package com.example.demo.aop;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * Aspect pour mesurer les performances des méthodes importantes
 * Utilise Micrometer pour enregistrer les métriques de performance
 */
@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class PerformanceMonitoringAspect {

    private final MeterRegistry meterRegistry;

    /**
     * Pointcut qui correspond aux contrôleurs REST
     */
    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void controllerPointcut() {
        // Méthode vide pour le pointcut
    }

    /**
     * Pointcut qui correspond aux méthodes des services
     */
    @Pointcut("within(@org.springframework.stereotype.Service *)")
    public void servicePointcut() {
        // Méthode vide pour le pointcut
    }

    /**
     * Mesure le temps d'exécution des méthodes de contrôleur
     * @param joinPoint Point d'entrée dans le code où l'aspect est appliqué
     * @return Le résultat de la méthode interceptée
     * @throws Throwable Si une exception se produit dans la méthode
     */
    @Around("controllerPointcut()")
    public Object measureControllerPerformance(ProceedingJoinPoint joinPoint) throws Throwable {
        return measureMethodPerformance(joinPoint, "http.server.requests");
    }

    /**
     * Mesure le temps d'exécution des méthodes de service
     * @param joinPoint Point d'entrée dans le code où l'aspect est appliqué
     * @return Le résultat de la méthode interceptée
     * @throws Throwable Si une exception se produit dans la méthode
     */
    @Around("servicePointcut()")
    public Object measureServicePerformance(ProceedingJoinPoint joinPoint) throws Throwable {
        return measureMethodPerformance(joinPoint, "service.execution");
    }

    /**
     * Méthode utilitaire pour mesurer les performances
     * @param joinPoint Point d'entrée dans le code
     * @param metricPrefix Préfixe pour le nom de la métrique
     * @return Le résultat de la méthode interceptée
     * @throws Throwable Si une exception se produit dans la méthode
     */
    private Object measureMethodPerformance(ProceedingJoinPoint joinPoint, String metricPrefix) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        String methodName = method.getName();
        String className = method.getDeclaringClass().getSimpleName();

        Timer.Sample sample = Timer.start(meterRegistry);

        try {
            Object result = joinPoint.proceed();
            long timeElapsed = sample.stop(
                Timer.builder(metricPrefix)
                    .tag("class", className)
                    .tag("method", methodName)
                    .tag("outcome", "SUCCESS")
                    .register(meterRegistry)
            );

            if (log.isDebugEnabled()) {
                log.debug("Performance de {}.{}: {} ms", className, methodName,
                    TimeUnit.NANOSECONDS.toMillis(timeElapsed));
            }

            return result;
        } catch (Exception e) {
            sample.stop(
                Timer.builder(metricPrefix)
                    .tag("class", className)
                    .tag("method", methodName)
                    .tag("outcome", "ERROR")
                    .tag("exception", e.getClass().getSimpleName())
                    .register(meterRegistry)
            );
            throw e;
        }
    }
}
