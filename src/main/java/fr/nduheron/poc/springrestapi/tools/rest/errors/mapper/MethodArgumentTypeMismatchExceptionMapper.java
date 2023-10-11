package fr.nduheron.poc.springrestapi.tools.rest.errors.mapper;

import fr.nduheron.poc.springrestapi.tools.rest.errors.dto.AttributeErrorDto;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;

import static java.util.Arrays.stream;
import static java.util.Collections.singletonList;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;

public class MethodArgumentTypeMismatchExceptionMapper implements BadRequestExceptionMapper {

    @Override
    public boolean canMap(Exception exception) {
        return exception instanceof MethodArgumentTypeMismatchException;
    }

    @Override
    public List<AttributeErrorDto> map(Exception exception) {
        List<AttributeErrorDto> errors;
        MethodArgumentTypeMismatchException ex = (MethodArgumentTypeMismatchException) exception;
        Class<?> requiredType = requireNonNull(ex.getRequiredType());
        if (requiredType.isEnum()) {
            errors = singletonList(new AttributeErrorDto("Allowable values: " + stream(requiredType.getEnumConstants()).map(Object::toString).collect(joining(", ")), null, ex.getName()));
        } else {
            errors = singletonList(new AttributeErrorDto(requireNonNull(ex.getRootCause()).getMessage(), null, ex.getName()));
        }
        return errors;
    }
}
