package fr.nduheron.poc.springrestapi.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;

import fr.nduheron.poc.springrestapi.tools.security.SecurityMatcher;
import fr.nduheron.poc.springrestapi.tools.swagger.DocketFactory;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.service.SecurityScheme;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Configuration de la documentation swagger
 *
 */
@Configuration
@EnableSwagger2
@Profile("!prod") // on n'active pas swagger en prod
@Import({ BeanValidatorPluginsConfiguration.class }) // plugin permettant d'ajouter javax.validation à la documentation
public class SwaggerConfiguration {

	@Value("${api.version}")
	private String version;

	@Value("${api.basePath}")
	private String apiBasePath;

	@Autowired
	private SecurityMatcher matcher;

	/**
	 * configuration de l'api "métier"
	 */
	@Bean
	public DocketFactory api() {
		DocketFactory factory = new DocketFactory();
		factory.setTitle("Spring Rest API");
		factory.setGroupName("Spring Rest API");
		factory.setSelector(PathSelectors.ant(apiBasePath + "/**"));
		factory.setVersion(version);
		factory.setSecurityContext(securityContext());
		factory.setSecurityScheme(apiKey());
		return factory;
	}

	@Bean
	SecurityScheme apiKey() {
		return new ApiKey("apiKey", HttpHeaders.AUTHORIZATION, "header");
	}

	/**
	 * Ajoute l'authentification sur les routes nécessitants une authentification
	 */
	@Bean
	SecurityContext securityContext() {
		return SecurityContext.builder().securityReferences(defaultAuth()).forPaths(matcher).build();
	}

	private List<SecurityReference> defaultAuth() {
		AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
		AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
		authorizationScopes[0] = authorizationScope;
		return Arrays.asList(new SecurityReference("apiKey", authorizationScopes));
	}

}
