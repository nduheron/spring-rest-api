package fr.nduheron.poc.springrestapi.config;

import fr.nduheron.poc.springrestapi.tools.rest.openapi.OpenAPIFactory;
import fr.nduheron.poc.springrestapi.tools.rest.openapi.OpenApiProperties;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;

/**
 * Configuration de la documentation swagger
 */
@Configuration
public class OpenApiConfiguration {
    public static final String OAUTH_PASSWORD_FLOW = "oauthPasswordFlow";

    @Bean
    public OpenAPIFactory userApi(Optional<BuildProperties> buildProperties, OpenApiProperties properties) {
        OpenAPIFactory factory = new OpenAPIFactory(buildProperties.orElse(null), properties);
        factory.addSecurityScheme(OAUTH_PASSWORD_FLOW,
                new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT"));
        return factory;
    }


}
