package fr.nduheron.poc.springrestapi.tools.openapi;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.MediaType;
import org.springdoc.core.customizers.OpenApiCustomiser;
import org.springframework.stereotype.Component;

@Component
public class JsonRequestCustomiser implements OpenApiCustomiser {

    @Override
    public void customise(OpenAPI openApi) {
        openApi.getPaths().values().stream().flatMap(pathItem -> pathItem.readOperations().stream())
                .filter(operation -> operation.getRequestBody() != null && operation.getRequestBody().getContent().containsKey("*/*"))
                .forEach(operation -> {
                    MediaType mediaType = operation.getRequestBody().getContent().remove("*/*");
                    operation.getRequestBody().getContent().addMediaType(org.springframework.http.MediaType.APPLICATION_JSON_VALUE, mediaType);
                });
    }
}
