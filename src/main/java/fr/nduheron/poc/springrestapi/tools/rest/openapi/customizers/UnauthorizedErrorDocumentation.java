package fr.nduheron.poc.springrestapi.tools.rest.openapi.customizers;

import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.responses.ApiResponse;
import org.apache.commons.collections4.CollectionUtils;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Component
@ConditionalOnProperty(value = "springdoc.api-docs.enabled", havingValue = "true", matchIfMissing = true)
@ConditionalOnClass(SecurityFilterChain.class)
public class UnauthorizedErrorDocumentation implements OperationCustomizer {
    @Override
    public Operation customize(Operation operation, HandlerMethod handlerMethod) {
        final String responseKey = String.valueOf(UNAUTHORIZED.value());
        if (CollectionUtils.isNotEmpty(operation.getSecurity()) && !operation.getResponses().containsKey(responseKey)) {
            operation.getResponses().addApiResponse(
                    responseKey,
                    new ApiResponse().description(UNAUTHORIZED.getReasonPhrase())
            );
        }
        return operation;
    }
}
