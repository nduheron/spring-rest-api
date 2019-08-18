package fr.nduheron.poc.springrestapi.tools.openapi;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.MediaType;
import org.springdoc.core.MediaAttributes;
import org.springdoc.core.ParameterBuilder;
import org.springdoc.core.RequestBodyBuilder;
import org.springdoc.core.RequestBuilder;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;

@Component
@Primary
public class CustomRequestBuilder extends RequestBuilder {

    public CustomRequestBuilder(ParameterBuilder parameterBuilder, RequestBodyBuilder requestBodyBuilder) {
        super(parameterBuilder, requestBodyBuilder);
    }

    @Override
    public Operation build(Components components, HandlerMethod handlerMethod, RequestMethod requestMethod, Operation operation, MediaAttributes mediaAttributes) {
        Operation customOperation = super.build(components, handlerMethod, requestMethod, operation, mediaAttributes);
        if (customOperation.getRequestBody() != null && customOperation.getRequestBody().getContent().containsKey("*/*")) {
            MediaType mediaType = customOperation.getRequestBody().getContent().remove("*/*");
            customOperation.getRequestBody().getContent().addMediaType(org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE, mediaType);
        }
        return customOperation;
    }
}
