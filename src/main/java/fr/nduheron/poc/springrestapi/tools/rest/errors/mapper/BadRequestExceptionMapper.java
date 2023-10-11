package fr.nduheron.poc.springrestapi.tools.rest.errors.mapper;

import fr.nduheron.poc.springrestapi.tools.rest.errors.dto.AttributeErrorDto;

import java.util.List;

public interface BadRequestExceptionMapper {

    boolean canMap(Exception exception);

    List<AttributeErrorDto> map(Exception exception);
}
