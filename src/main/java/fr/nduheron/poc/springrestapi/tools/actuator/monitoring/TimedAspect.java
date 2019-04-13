package fr.nduheron.poc.springrestapi.tools.actuator.monitoring;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.Timer;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * Intercepteur permettant de monitorer les métodes
 */
@Aspect
public class TimedAspect {
    private MeterRegistry registry;


    @Pointcut("execution(public * *(..))")
    public void publicMethod() {
    }

    @Pointcut("within(@io.micrometer.core.annotation.Timed *)")
    public void timeClassMethods() {
        // pointcut s'applicant à tous les bean avec l'annotation @Timed
    }

    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void controllerClassMethods() {
        // pointcut s'applicant à tous les controller REST
    }

    @Pointcut("within(@org.springframework.stereotype.Service *)")
    public void serviceClassMethods() {
        // pointcut s'applicant à tous les services métier
    }

    @Around("publicMethod() && controllerClassMethods()")
    public Object timedControllersMethod(ProceedingJoinPoint pjp) throws Throwable {
        return timedMethod(pjp, "controllers");
    }

    @Around("publicMethod() && serviceClassMethods()")
    public Object timedServicesMethod(ProceedingJoinPoint pjp) throws Throwable {
        return timedMethod(pjp, "services");
    }

    @Around("publicMethod() && timeClassMethods()")
    public Object timedCustomMethod(ProceedingJoinPoint pjp) throws Throwable {
        return timedMethod(pjp, "custom");
    }

    private Object timedMethod(ProceedingJoinPoint pjp, String name) throws Throwable {
        Timer.Sample sample = Timer.start(registry);
        try {
            return pjp.proceed();
        } finally {
            sample.stop(Timer.builder(name).tags(Tags.of("class", pjp.getStaticPart().getSignature().getDeclaringTypeName(), "method",
                    pjp.getStaticPart().getSignature().getName())).register(registry));
        }
    }

    public void setRegistry(MeterRegistry registry) {
        this.registry = registry;
    }
}
