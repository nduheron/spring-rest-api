package fr.nduheron.poc.springrestapi.config;//package fr.nduheron.poc.springrestapi.config;

import fr.nduheron.poc.springrestapi.tools.exception.model.Error;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.Arrays;

/**
 * Configuration de la documentation swagger
 */
@Configuration
public class OpenApiConfiguration {
    public static final String DEFAULT_BAD_REQUEST = "400";
    public static final String INVALID_FORMAT = "InvalidFormat";
    public static final String OAUTH_PASSWORD_FLOW = "oauthPasswordFlow";

    @Bean
    public OpenAPI userApi(@Value("${token.url}") String tokenUrl, BuildProperties buildProperties) {
        return new OpenAPI()
                .info(new Info()
                        .title("User API")
                        .version(buildProperties.getVersion())
                ).components(new Components()
                        .addResponses(DEFAULT_BAD_REQUEST, new ApiResponse().description(HttpStatus.BAD_REQUEST.getReasonPhrase()).content(new Content().addMediaType(MediaType.ALL_VALUE, new io.swagger.v3.oas.models.media.MediaType().schema(new ArraySchema().items(new Schema<>().$ref("Error")))
                                .addExamples(INVALID_FORMAT, new Example().$ref(INVALID_FORMAT)))))
                        .addExamples(INVALID_FORMAT, new Example()
                                .description("Des paramètres de la requête sont invalides.")
                                .value(Arrays.asList(
                                        new Error(Error.INVALID_FORMAT, "not a well-formed email address", "email"),
                                        new Error(Error.INVALID_FORMAT, "may not be null", "username"))))
                        .addSecuritySchemes(OAUTH_PASSWORD_FLOW,
                                new SecurityScheme().type(SecurityScheme.Type.OAUTH2)
                                        .flows(new OAuthFlows().password(new OAuthFlow().tokenUrl(tokenUrl))))
                );
    }


}
