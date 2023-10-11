package fr.nduheron.poc.springrestapi.tools.rest.openapi.customizers;

import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.responses.ApiResponse;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;

import java.lang.reflect.Parameter;
import java.util.List;

import static java.util.Arrays.stream;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Component
@ConditionalOnProperty(value = "springdoc.api-docs.enabled", havingValue = "true", matchIfMissing = true)
public class NotFoundErrorDocumentation implements OperationCustomizer {

    @Override
    public Operation customize(Operation operation, HandlerMethod handlerMethod) {
        final String responseKey = String.valueOf(NOT_FOUND.value());
        if (!operation.getResponses().containsKey(responseKey)) {
            List<Parameter> parameters = stream(handlerMethod.getMethod().getParameters())
                    .filter(it -> it.isAnnotationPresent(PathVariable.class))
                    .collect(toList());
            boolean isDeleteOperation = stream(requireNonNull(handlerMethod.getMethodAnnotation(RequestMapping.class)).method()).anyMatch(it -> it == RequestMethod.DELETE);

            if (parameters.size() > 1 || (parameters.size() == 1 && !isDeleteOperation)) {
                operation.getResponses().addApiResponse(
                        responseKey,
                        new ApiResponse()
                                .description(NOT_FOUND.getReasonPhrase())
                );
            }
        }
        return operation;
    }

}
