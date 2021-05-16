package fr.nduheron.poc.springrestapi.tools.openapi;

import io.swagger.v3.oas.models.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import javax.annotation.security.RolesAllowed;
import java.lang.reflect.Method;

import static org.springframework.core.annotation.AnnotatedElementUtils.findMergedAnnotation;


@Component
public class CleanForbiddenGenericResponse extends AbstractCleanGenericResponse {

    public CleanForbiddenGenericResponse() {
        super(HttpStatus.FORBIDDEN);
    }

    @Override
    boolean isValid(Operation operation, Method method) {
        return findMergedAnnotation(method, PreAuthorize.class) != null || findMergedAnnotation(method, RolesAllowed.class) != null;
    }
}
