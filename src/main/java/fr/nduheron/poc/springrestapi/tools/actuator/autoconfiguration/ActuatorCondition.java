package fr.nduheron.poc.springrestapi.tools.actuator.autoconfiguration;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * Condition pour savoir si actuator est actif ou non
 */
public class ActuatorCondition implements Condition {

	@Override
	public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
		String isActuatorEnable = context.getEnvironment().getProperty("management.endpoints.enabled-by-default");
		return isActuatorEnable == null || "true".equals(isActuatorEnable);
	}

}
