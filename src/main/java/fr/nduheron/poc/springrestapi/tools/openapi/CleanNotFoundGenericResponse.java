package fr.nduheron.poc.springrestapi.tools.openapi;

import io.swagger.v3.core.util.ReflectionUtils;
import io.swagger.v3.oas.models.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.reflect.Method;
import java.util.Arrays;

import static java.util.Arrays.stream;


@Component
public class CleanNotFoundGenericResponse extends AbstractCleanGenericResponse {

    public CleanNotFoundGenericResponse() {
        super(HttpStatus.NOT_FOUND);
    }

    @Override
    boolean isValid(Operation operation, Method method) {
        long pathVariablesCount = stream(method.getParameterAnnotations()).flatMap(Arrays::stream).filter(annotation -> annotation.annotationType().equals(PathVariable.class)).count();
        RequestMapping reqMappingMethod = ReflectionUtils.getAnnotation(method, RequestMapping.class);
        return pathVariablesCount > 1 || (pathVariablesCount == 1 && stream(reqMappingMethod.method()).noneMatch(httpMethod -> httpMethod == RequestMethod.DELETE));
    }
}
