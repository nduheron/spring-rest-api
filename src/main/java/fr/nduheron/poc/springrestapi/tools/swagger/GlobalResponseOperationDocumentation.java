package fr.nduheron.poc.springrestapi.tools.swagger;

import com.google.common.collect.Sets;
import io.swagger.annotations.ApiResponses;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.service.ResponseMessage;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.OperationBuilderPlugin;
import springfox.documentation.spi.service.contexts.OperationContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.common.SwaggerPluginSupport;

import java.util.Optional;
import java.util.Set;

import static java.util.Arrays.stream;

/**
 * Gestion automatique de la documentation swagger pour les erreurs.
 */
@Component
@Order(SwaggerPluginSupport.SWAGGER_PLUGIN_ORDER + 1)
@ConditionalOnClass(Docket.class)
public class GlobalResponseOperationDocumentation implements OperationBuilderPlugin {

    @Override
    public boolean supports(DocumentationType delimiter) {
        return true;
    }

    @Override
    public void apply(OperationContext context) {
        if (context.findControllerAnnotation(RestController.class).isPresent()) {
            Set<ResponseMessage> responsesMessage = Sets.newHashSet();

            // BAD REQUEST
//            Optional<ApiBadRequestResponse> optionalBadRequest = context
//                    .findAnnotation(ApiBadRequestResponse.class).transform(Optional::of).or(Optional.empty());
//            if (optionalBadRequest.isPresent() && customDocumentationIsAbsent(context, HttpStatus.BAD_REQUEST)) {
//                responsesMessage.add(new ResponseMessageBuilder().code(HttpStatus.BAD_REQUEST.value())
//                        .message("Il y a une(des) erreur(s) dans la requête.")
//                        .vendorExtensions(Collections.singletonList(errorDocumentationHelper.buildExamples(optionalBadRequest.get().value())))
//                        .responseModel(new ModelRef(Error.class.getSimpleName())).build());
//            }

            // NOT FOUND
            long cptNotFound = context.getParameters().stream()
                    .filter(methodParameter -> methodParameter.findAnnotation(PathVariable.class).isPresent()).count();
            if (cptNotFound > 0 && (context.httpMethod() != HttpMethod.DELETE || cptNotFound > 1) && customDocumentationIsAbsent(context, HttpStatus.NOT_FOUND)) {
                responsesMessage.add(new ResponseMessageBuilder().code(HttpStatus.NOT_FOUND.value())
                        .message("La ressource n'existe pas.").build());
            }

            // Erreur technique
            if (customDocumentationIsAbsent(context, HttpStatus.INTERNAL_SERVER_ERROR)) {
                responsesMessage.add(new ResponseMessageBuilder().code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .message("Une erreur inattendue s'est produite.").build());

                context.operationBuilder().responseMessages(responsesMessage);
            }
        }
    }


    private boolean customDocumentationIsAbsent(OperationContext context, HttpStatus httpStatus) {
        Optional<ApiResponses> annotation = context.findAnnotation(ApiResponses.class).transform(Optional::of).or(Optional.empty());
        return annotation.map(apiResponses -> stream(apiResponses.value()).noneMatch(apiResponse -> apiResponse.code() == httpStatus.value())).orElse(true);
    }
}
