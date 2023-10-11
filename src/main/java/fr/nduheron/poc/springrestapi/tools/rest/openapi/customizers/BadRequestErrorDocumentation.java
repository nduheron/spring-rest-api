package fr.nduheron.poc.springrestapi.tools.rest.openapi.customizers;

import fr.nduheron.poc.springrestapi.tools.rest.errors.dto.AttributeErrorDto;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.method.HandlerMethod;

import static java.util.Arrays.stream;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Component
@ConditionalOnProperty(value = "springdoc.api-docs.enabled", havingValue = "true", matchIfMissing = true)
public class BadRequestErrorDocumentation implements OperationCustomizer {

    @Override
    public Operation customize(Operation operation, HandlerMethod handlerMethod) {
        final String responseKey = String.valueOf(BAD_REQUEST.value());

        if (!operation.getResponses().containsKey(responseKey) && stream(handlerMethod.getMethod().getParameters()).anyMatch(it -> it.isAnnotationPresent(RequestBody.class))) {
            operation.getResponses().addApiResponse(
                    responseKey,
                    new ApiResponse()
                            .description(BAD_REQUEST.getReasonPhrase())
                            .content(new Content().addMediaType(
                                            APPLICATION_JSON_VALUE,
                                            new MediaType()
                                                    .schema(new Schema<>().type("array").items(new Schema<AttributeErrorDto>().$ref("AttributeError")))
                                    )
                            )
            );

        }
        return operation;
    }

}
