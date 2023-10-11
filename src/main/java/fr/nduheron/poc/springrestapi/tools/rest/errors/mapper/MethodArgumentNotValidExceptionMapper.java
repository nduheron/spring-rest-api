package fr.nduheron.poc.springrestapi.tools.rest.errors.mapper;

import fr.nduheron.poc.springrestapi.tools.rest.errors.dto.AttributeErrorDto;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.stream.Collectors;

public class MethodArgumentNotValidExceptionMapper implements BadRequestExceptionMapper {
    @Override
    public boolean canMap(Exception exception) {
        return exception instanceof MethodArgumentNotValidException;
    }

    @Override
    public List<AttributeErrorDto> map(Exception exception) {
        return ((MethodArgumentNotValidException) exception).getBindingResult().getAllErrors()
                .stream().map(it -> {
                    FieldError fieldError = (FieldError) it;
                    String[] path = StringUtils.splitPreserveAllTokens(fieldError.getField(), ".");
                    return new AttributeErrorDto(fieldError.getDefaultMessage(), fieldError.getField(), path[path.length - 1]);
                })
                .collect(Collectors.toList());

    }
}
