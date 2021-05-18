package fr.nduheron.poc.springrestapi.tools.openapi;

import io.swagger.v3.oas.models.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import java.lang.reflect.Method;
import java.util.Arrays;

import static java.util.Arrays.stream;


@Component
public class CleanBadRequestGenericResponse extends AbstractCleanGenericResponse {

    public CleanBadRequestGenericResponse() {
        super(HttpStatus.BAD_REQUEST);
    }

    @Override
    boolean isValid(Operation operation, Method method) {
        return stream(method.getParameterAnnotations())
                .flatMap(Arrays::stream)
                .anyMatch(annotation -> annotation.annotationType().equals(RequestBody.class));
    }
}
