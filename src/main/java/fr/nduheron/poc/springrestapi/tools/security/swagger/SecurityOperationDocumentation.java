package fr.nduheron.poc.springrestapi.tools.security.swagger;

import fr.nduheron.poc.springrestapi.tools.security.SecurityMatcher;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.stereotype.Component;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.service.ResponseMessage;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.OperationBuilderPlugin;
import springfox.documentation.spi.service.contexts.OperationContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.common.SwaggerPluginSupport;

import javax.annotation.security.RolesAllowed;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static java.util.Arrays.stream;

/**
 * Gestion automatique de la documentation swagger pour les erreurs liées à la
 * sécurité.
 */
@Component
@Order(SwaggerPluginSupport.SWAGGER_PLUGIN_ORDER + 10)
@ConditionalOnClass({Docket.class, WebSecurityConfigurerAdapter.class})
@ConditionalOnProperty(value = "security.config.enable", havingValue = "true")
public class SecurityOperationDocumentation implements OperationBuilderPlugin {

    @Autowired
    private SecurityMatcher matcher;

    @Override
    public boolean supports(DocumentationType delimiter) {
        return true;
    }

    @Override
    public void apply(OperationContext context) {
        Set<ResponseMessage> responsesMessage = new HashSet<>();
        if (matcher.apply(context.requestMappingPattern())) {
            // UNAUTHORIZED
            if (customDocumentationIsAbsent(context, HttpStatus.UNAUTHORIZED)) {
                ResponseMessage unauthorized = new ResponseMessageBuilder().code(HttpStatus.UNAUTHORIZED.value())
                        .message("Utilisateur non authentifié.").build();
                responsesMessage.add(unauthorized);
            }
            // FORBIDDEN
            if (hasAuthorize(context) && customDocumentationIsAbsent(context, HttpStatus.FORBIDDEN)) {
                ResponseMessageBuilder message = new ResponseMessageBuilder().code(HttpStatus.FORBIDDEN.value())
                        .message("Utilisateur authentifié mais droits insuffisants.");
                ResponseMessage forbidden = message.build();
                responsesMessage.add(forbidden);
            }
            context.operationBuilder().responseMessages(responsesMessage);
        }
    }

    private boolean hasAuthorize(OperationContext context) {
        return context.findControllerAnnotation(PreAuthorize.class).isPresent()
                || context.findControllerAnnotation(PostAuthorize.class).isPresent()
                || context.findAnnotation(PreAuthorize.class).isPresent()
                || context.findAnnotation(PostAuthorize.class).isPresent()
                || context.findAnnotation(RolesAllowed.class).isPresent()
                || context.findAnnotation(RolesAllowed.class).isPresent();
    }

    private boolean customDocumentationIsAbsent(OperationContext context, HttpStatus httpStatus) {
        Optional<ApiResponses> annotation = context.findAnnotation(ApiResponses.class).transform(Optional::of).or(Optional.empty());
        return annotation.map(apiResponses -> stream(apiResponses.value()).noneMatch(apiResponse -> apiResponse.code() == httpStatus.value())).orElse(true);
    }
}
