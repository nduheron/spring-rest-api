package fr.nduheron.poc.springrestapi.tools.openapi;

import io.swagger.v3.core.util.ReflectionUtils;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.models.Operation;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.http.HttpStatus;
import org.springframework.web.method.HandlerMethod;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static io.swagger.v3.core.util.ReflectionUtils.getAnnotation;
import static java.util.Arrays.stream;
import static java.util.Optional.ofNullable;

public abstract class AbstractCleanGenericResponse implements OperationCustomizer {
    private final HttpStatus httpStatus;

    protected AbstractCleanGenericResponse(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    @Override
    public Operation customize(Operation operation, HandlerMethod handlerMethod) {
        List<ApiResponse> apiResponseAnnotations = ofNullable(getAnnotation(handlerMethod.getMethod(), io.swagger.v3.oas.annotations.responses.ApiResponses.class))
                .map(responses -> stream(responses.value()).collect(Collectors.toList()))
                .orElse(
                        ofNullable(ReflectionUtils.getRepeatableAnnotations(handlerMethod.getMethod(), ApiResponse.class))
                                .orElse(Collections.emptyList())
                );

        String key = String.valueOf(httpStatus.value());
        if (operation.getResponses().containsKey(key) && !isValid(operation, handlerMethod.getMethod()) && apiResponseAnnotations.stream().noneMatch(apiResponse -> key.equals(apiResponse.responseCode()))) {
            operation.getResponses().remove(key);
        }
        return operation;
    }

    abstract boolean isValid(Operation operation, Method method);

}
