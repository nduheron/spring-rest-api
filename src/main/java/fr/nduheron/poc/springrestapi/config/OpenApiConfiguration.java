package fr.nduheron.poc.springrestapi.config;//package fr.nduheron.poc.springrestapi.config;

import com.google.common.collect.Lists;
import fr.nduheron.poc.springrestapi.tools.exception.model.Error;
import fr.nduheron.poc.springrestapi.tools.security.SecurityMatcher;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration de la documentation swagger
 */
@Configuration
public class OpenApiConfiguration {

    @Autowired
    private SecurityMatcher matcher;

    @Autowired
    private BuildProperties buildProperties;

    @Bean
    public OpenAPI userApi(@Value("${token.url}") String tokenUrl) {
        return new OpenAPI()
                .info(new Info()
                        .title("User API")
                        .version(buildProperties.getVersion())
                ).components(new Components()
                        .addExamples("InvalidFormat", new Example()
                                .description("Des paramêtres de la requête sont invalides.")
                                .value(Lists.newArrayList(
                                        new Error(Error.INVALID_FORMAT, "not a well-formed email address", "email"),
                                        new Error(Error.INVALID_FORMAT, "may not be null", "username"))))
                        .addSecuritySchemes("oauthPasswordFlow",
                                new SecurityScheme().type(SecurityScheme.Type.OAUTH2)
                                        .flows(new OAuthFlows().password(new OAuthFlow().tokenUrl(tokenUrl))))
                );
    }


}
