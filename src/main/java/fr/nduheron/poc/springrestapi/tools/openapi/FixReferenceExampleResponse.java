package fr.nduheron.poc.springrestapi.tools.openapi;

import io.swagger.v3.core.util.ReflectionUtils;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.examples.Example;
import org.apache.commons.lang3.StringUtils;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static io.swagger.v3.core.util.ReflectionUtils.getAnnotation;
import static java.util.Arrays.stream;
import static java.util.Optional.ofNullable;

/**
 * Springdoc openapi ne prend pas les références des exemples <pre>@ExampleObject(ref = "InvalidFormat")</pre> ce composant permet de régler ce problème
 */
@Component
@ConditionalOnBean(OpenAPI.class)
public class FixReferenceExampleResponse implements OperationCustomizer {

    @Override
    public Operation customize(Operation operation, HandlerMethod handlerMethod) {
        List<ApiResponse> apiResponseAnnotations = ofNullable(getAnnotation(handlerMethod.getMethod(), io.swagger.v3.oas.annotations.responses.ApiResponses.class))
                .map(responses -> stream(responses.value()).collect(Collectors.toList()))
                .orElse(
                        ofNullable(ReflectionUtils.getRepeatableAnnotations(handlerMethod.getMethod(), io.swagger.v3.oas.annotations.responses.ApiResponse.class))
                                .orElse(Collections.emptyList())
                );

        apiResponseAnnotations.forEach(annotation -> stream(annotation.content())
                .flatMap(contentAnnotation -> stream(contentAnnotation.examples()))
                .filter(annotationExample -> StringUtils.isNotBlank(annotationExample.ref()))
                .forEach(annotationExample -> operation.getResponses().get(annotation.responseCode()).getContent().get(MediaType.ALL_VALUE).addExamples(annotationExample.ref(), new Example().$ref(annotationExample.ref()))));
        return operation;
    }
}
