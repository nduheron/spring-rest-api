package fr.nduheron.poc.springrestapi.tools.rest.errors.mapper;

import fr.nduheron.poc.springrestapi.tools.rest.errors.dto.AttributeErrorDto;
import org.springframework.web.bind.MissingServletRequestParameterException;

import java.util.List;

import static java.util.Collections.singletonList;

public class MissingServletRequestParameterExceptionMapper implements BadRequestExceptionMapper {

    @Override
    public boolean canMap(Exception exception) {
        return exception instanceof MissingServletRequestParameterException;
    }

    @Override
    public List<AttributeErrorDto> map(Exception exception) {
        MissingServletRequestParameterException ex = (MissingServletRequestParameterException) exception;
        return singletonList(new AttributeErrorDto(ex.getMessage(), null, ex.getParameterName()));
    }
}
