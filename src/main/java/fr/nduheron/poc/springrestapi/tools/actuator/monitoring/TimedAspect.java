package fr.nduheron.poc.springrestapi.tools.actuator.monitoring;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.Timer;

/**
 * Intercepteur permettant de monitorer les métodes
 *
 */
@Aspect
@Component
public class TimedAspect {
	private final MeterRegistry registry;

	public TimedAspect(MeterRegistry registry) {
		this.registry = registry;
	}

	@Pointcut("within(@org.springframework.stereotype.Repository *)")
	public void repositoryClassMethods() {
	}

	@Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
	public void controllerClassMethods() {
	}

	@Pointcut("within(@org.springframework.stereotype.Service *)")
	public void serviceClassMethods() {
	}

	@Around("controllerClassMethods() || repositoryClassMethods() || serviceClassMethods()")
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
