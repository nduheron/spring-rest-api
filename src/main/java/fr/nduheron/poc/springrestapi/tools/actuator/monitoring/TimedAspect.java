package fr.nduheron.poc.springrestapi.tools.actuator.monitoring;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
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

	/**
	 * Monitoring des controllers
	 */
	@Around("execution(public * fr.nduheron..controller..*(..))")
	public Object timedControllersMethod(ProceedingJoinPoint pjp) throws Throwable {
		return timedMethod("services", "Monitoring des controllers", pjp);
	}

	/**
	 * Monitoring des DAOs
	 */
	@Around("execution(public * fr.nduheron..repository..*(..))")
	public Object timedRepositoriesMethod(ProceedingJoinPoint pjp) throws Throwable {
		return timedMethod("repositories", "Monitoring des DAOs", pjp);
	}

	private Object timedMethod(String tag, String description, ProceedingJoinPoint pjp) throws Throwable {
		Timer.Sample sample = Timer.start(registry);
		try {
			return pjp.proceed();
		} finally {
			sample.stop(Timer.builder(tag).description(description)
					.tags(Tags.of("class", pjp.getStaticPart().getSignature().getDeclaringTypeName(), "method",
							pjp.getStaticPart().getSignature().getName()))
					.publishPercentileHistogram(true).publishPercentiles(0.9, 0.95, 0.99).register(registry));
		}
	}
}
