package fr.nduheron.poc.springrestapi.tools.rest.errors.mapper;

import fr.nduheron.poc.springrestapi.tools.rest.errors.dto.AttributeErrorDto;
import org.springframework.web.bind.MissingRequestHeaderException;

import java.util.List;

import static java.util.Collections.singletonList;

public class MissingRequestHeaderExceptionMapper implements BadRequestExceptionMapper {

    @Override
    public boolean canMap(Exception exception) {
        return exception instanceof MissingRequestHeaderException;
    }

    @Override
    public List<AttributeErrorDto> map(Exception exception) {
        return singletonList(new AttributeErrorDto("Missing request header", null, ((MissingRequestHeaderException) exception).getHeaderName()));
    }
}
