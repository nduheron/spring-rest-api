package fr.nduheron.poc.springrestapi.tools.rest.errors.mapper;

import fr.nduheron.poc.springrestapi.tools.rest.errors.dto.AttributeErrorDto;

import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.stream.Collectors;

public class ConstraintViolationExceptionMapper implements BadRequestExceptionMapper {

    @Override
    public boolean canMap(Exception exception) {
        return exception instanceof ConstraintViolationException;
    }

    @Override
    public List<AttributeErrorDto> map(Exception exception) {
        return ((ConstraintViolationException) exception).getConstraintViolations().stream()
                .map(it -> {
                    String[] path = splitPreserveAllTokens(it.getPropertyPath().toString(), ".");
                    String attribute = path[path.length - 1];
                    return new AttributeErrorDto(it.getMessage(), it.getPropertyPath().toString(), attribute);
                })
                .collect(Collectors.toList());
    }
}
