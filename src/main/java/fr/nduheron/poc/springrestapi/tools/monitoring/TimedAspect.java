package fr.nduheron.poc.springrestapi.tools.monitoring;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.Timer;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * Intercepteur permettant de monitorer les métodes
 */
@Aspect
@Component
public class TimedAspect {
    private final MeterRegistry registry;

    public TimedAspect(MeterRegistry registry) {
        this.registry = registry;
    }

    //@Pointcut("within(@io.micrometer.core.annotation.Timed *)")
    @Pointcut("@annotation(io.micrometer.core.annotation.Timed)")
    public void timeClassMethods() {
        // pointcut s'applicant à tous les bean avec l'annotation @Timed
    }

    @Pointcut("@within(org.springframework.stereotype.Repository)")
    public void repositoryClassMethods() {
        // pointcut s'applicant à tous les DAOs
    }

    @Pointcut("@annotation(org.springframework.stereotype.Repository)")
    public void serviceClassMethods() {
        // pointcut s'applicant à tous les services métier
    }

    @Around("execution(public * javax.persistence.EntityManager.*(..))")
    public Object timedEntityManagerMethod(ProceedingJoinPoint pjp) throws Throwable {
        return timedMethod(pjp, "EntityManager");
    }


    @Around("@annotation(io.micrometer.core.annotation.Timed)")
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

}
