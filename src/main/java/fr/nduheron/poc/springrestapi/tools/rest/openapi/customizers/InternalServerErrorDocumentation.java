package fr.nduheron.poc.springrestapi.tools.rest.openapi.customizers;

import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.responses.ApiResponse;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Component
@ConditionalOnProperty(value = "springdoc.api-docs.enabled", havingValue = "true", matchIfMissing = true)
public class InternalServerErrorDocumentation implements OperationCustomizer {
    @Override
    public Operation customize(Operation operation, HandlerMethod handlerMethod) {
        final String responseKey = String.valueOf(INTERNAL_SERVER_ERROR.value());
        if (!operation.getResponses().containsKey(responseKey)) {
            operation.getResponses().addApiResponse(
                    responseKey,
                    new ApiResponse().description(INTERNAL_SERVER_ERROR.getReasonPhrase())
            );
        }
        return operation;
    }
}
