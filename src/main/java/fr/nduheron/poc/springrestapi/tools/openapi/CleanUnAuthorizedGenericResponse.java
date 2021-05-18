package fr.nduheron.poc.springrestapi.tools.openapi;

import io.swagger.v3.oas.models.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;


@Component
public class CleanUnAuthorizedGenericResponse extends AbstractCleanGenericResponse {

    public CleanUnAuthorizedGenericResponse() {
        super(HttpStatus.UNAUTHORIZED);
    }

    @Override
    boolean isValid(Operation operation, Method method) {
        return isNotEmpty(operation.getSecurity());
    }
}
