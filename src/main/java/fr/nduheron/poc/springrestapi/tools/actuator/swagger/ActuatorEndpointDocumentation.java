package fr.nduheron.poc.springrestapi.tools.actuator.swagger;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.fasterxml.classmate.TypeResolver;

import springfox.documentation.schema.ModelRef;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.ParameterBuilderPlugin;
import springfox.documentation.spi.service.contexts.ParameterContext;
import springfox.documentation.swagger.common.SwaggerPluginSupport;

/**
 * Springfox ne gère pas bien la documentation des Endpoints actuator qui
 * contiennent tous une map dans le body et ne propose pas de champ pour la
 * saisie du "pathparam" quand il y en a un. Ce plugin corrige ce problème.
 */
@Component
@Order(SwaggerPluginSupport.SWAGGER_PLUGIN_ORDER + 1000)
@ConditionalOnBean(name = "adminApi")
public class ActuatorEndpointDocumentation implements ParameterBuilderPlugin {

	private static Pattern pattern = Pattern.compile("^(?:.+)+\\/\\{(.+)\\}$");

	@Value("${management.endpoints.web.base-path:/actuator}")
	private String actuatorBasePath;

	@Override
	public boolean supports(DocumentationType delimiter) {
		return true;
	}

	@Override
	public void apply(ParameterContext parameterContext) {
		if (parameterContext.getOperationContext().requestMappingPattern().startsWith(actuatorBasePath)) {
			Matcher matcher = pattern.matcher(parameterContext.getOperationContext().requestMappingPattern());
			if (matcher.matches()) {
				// Ajoute le paramètre de type path à la documentation swagger
				parameterContext.parameterBuilder().type(new TypeResolver().resolve(String.class))
						.description(matcher.group(1)).name(matcher.group(1)).parameterType("path")
						.parameterAccess("access").required(true).modelRef(new ModelRef("string")).build();
			} else {
				// on cache le body
				parameterContext.parameterBuilder().hidden(true);
			}
		}
	}

}
