package fr.nduheron.poc.springrestapi.tools.swagger;

import com.google.common.collect.Sets;
import fr.nduheron.poc.springrestapi.tools.exception.model.Error;
import fr.nduheron.poc.springrestapi.tools.swagger.annotations.ApiBadRequestResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ResponseMessage;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.OperationBuilderPlugin;
import springfox.documentation.spi.service.contexts.OperationContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.common.SwaggerPluginSupport;

import java.util.Collections;
import java.util.Set;

/**
 * Gestion automatique de la documentation swagger pour les erreurs.
 */
@Component
@Order(SwaggerPluginSupport.SWAGGER_PLUGIN_ORDER)
@ConditionalOnClass(Docket.class)
public class GlobalResponseOperationDocumentation implements OperationBuilderPlugin {

    private ErrorExampleHelper errorDocumentationHelper;

    public GlobalResponseOperationDocumentation(ErrorExampleHelper errorDocumentationHelper) {
        this.errorDocumentationHelper = errorDocumentationHelper;
    }

    @Override
    public boolean supports(DocumentationType delimiter) {
        return true;
    }

    @Override
    public void apply(OperationContext context) {
        if (context.findControllerAnnotation(RestController.class).isPresent()) {
            Set<ResponseMessage> responsesMessage = Sets.newHashSet();

            // BAD REQUEST
            com.google.common.base.Optional<ApiBadRequestResponse> optionalBadRequest = context
                    .findAnnotation(ApiBadRequestResponse.class);
            if (optionalBadRequest.isPresent()) {
                responsesMessage.add(new ResponseMessageBuilder().code(HttpStatus.BAD_REQUEST.value())
                        .message("Il y a une(des) erreur(s) dans la requête.")
                        .vendorExtensions(Collections.singletonList(errorDocumentationHelper.buildExamples(optionalBadRequest.get().value())))
                        .responseModel(new ModelRef(Error.class.getSimpleName())).build());
            }

            // NOT FOUND
            long cptNotFound = context.getParameters().stream()
                    .filter(methodParameter -> methodParameter.findAnnotation(PathVariable.class).isPresent()).count();
            if (cptNotFound > 0 && (context.httpMethod() != HttpMethod.DELETE || cptNotFound > 1)) {
                responsesMessage.add(new ResponseMessageBuilder().code(HttpStatus.NOT_FOUND.value())
                        .message("La ressource n'existe pas.").build());
            }

            // Erreur technique
            responsesMessage.add(new ResponseMessageBuilder().code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("Une erreur inattendue s'est produite.").build());

            context.operationBuilder().responseMessages(responsesMessage);
        }
    }
}
