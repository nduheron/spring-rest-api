package fr.nduheron.poc.springrestapi.tools.actuator.autoconfiguration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

import fr.nduheron.poc.springrestapi.tools.swagger.DocketFactory;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * Configuration de la documentation swagger pour actuator
 *
 */
@Configuration
@Conditional(ActuatorCondition.class) // on configure la doc swagger seulement si actuator est activé
@ConditionalOnBean(Docket.class) // on ajoute actuator à swagger seulement si swagger est configuré
public class SwaggerAutoConfiguration {

	@Value("${management.endpoints.web.base-path:/actuator/**}")
	private String actuatorBasePathPattern;

	@Bean
	public DocketFactory adminApi() {
		DocketFactory factory = new DocketFactory();
		factory.setTitle("Administration API");
		factory.setGroupName("zz-admin-api");
		factory.setDescription("Consulter les ressources systèmes.");
		factory.setSelector(PathSelectors.ant(actuatorBasePathPattern));
		return factory;
	}

}
