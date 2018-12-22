package fr.nduheron.poc.springrestapi.tools.actuator.monitoring;

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

    @Pointcut("within(@io.micrometer.core.annotation.Timed *)")
    public void timeClassMethods() {
        // pointcut s'applicant à tous les bean avec l'annotation @Timed
    }

    @Pointcut("within(@org.springframework.stereotype.Repository *)")
    public void repositoryClassMethods() {
        // pointcut s'applicant à tous les DAOs
    }

    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void controllerClassMethods() {
        // pointcut s'applicant à tous les controller REST
    }

    @Pointcut("within(@org.springframework.stereotype.Service *)")
    public void serviceClassMethods() {
        // pointcut s'applicant à tous les services métier
    }

    @Pointcut("within(@org.mapstruct.Mapper *)")
    public void mapperClassMethods() {
        // pointcut s'applicant à tous les mapper mapstruct
    }

    @Around("controllerClassMethods() || repositoryClassMethods() || serviceClassMethods() || timeClassMethods() || mapperClassMethods()")
    public Object timedSericesMethod(ProceedingJoinPoint pjp) throws Throwable {
        Timer.Sample sample = Timer.start(registry);
        try {
            return pjp.proceed();
        } finally {
            sample.stop(Timer.builder("services").description("Monitoring des controllers, services et repositories")
                    .tags(Tags.of("class", pjp.getStaticPart().getSignature().getDeclaringTypeName(), "method",
                            pjp.getStaticPart().getSignature().getName())).register(registry));
        }
    }

}
