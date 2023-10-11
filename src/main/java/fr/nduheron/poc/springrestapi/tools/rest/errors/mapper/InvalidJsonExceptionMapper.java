package fr.nduheron.poc.springrestapi.tools.rest.errors.mapper;

import com.fasterxml.jackson.databind.JsonMappingException;
import fr.nduheron.poc.springrestapi.tools.rest.errors.dto.AttributeErrorDto;
import org.springframework.http.converter.HttpMessageNotReadableException;

import java.util.List;

import static java.util.Collections.singletonList;

public class InvalidJsonExceptionMapper implements BadRequestExceptionMapper {

    @Override
    public boolean canMap(Exception exception) {
        return exception instanceof JsonMappingException || exception instanceof HttpMessageNotReadableException;
    }

    @Override
    public List<AttributeErrorDto> map(Exception exception) {
        return singletonList(new AttributeErrorDto("Invalid JSON.", null, null));
    }

}
