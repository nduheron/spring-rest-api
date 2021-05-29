package fr.nduheron.poc.springrestapi.tools.openapi;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.springdoc.core.customizers.OpenApiCustomiser;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

/**
 * Remplace tout les media type par défaut (all value) par un media type json
 */
@Component
@ConditionalOnBean(OpenAPI.class)
public class JsonDefaultMediaType implements OpenApiCustomiser {

    @Override
    public void customise(OpenAPI openApi) {
        openApi.getPaths().values().stream()
                .flatMap(pathItem -> pathItem.readOperations().stream())
                .forEach(operation -> {
                    processRequestBody(operation.getRequestBody());
                    processApiResponses(operation.getResponses());
                });
    }

    private void processRequestBody(RequestBody requestBody) {
        if (requestBody != null) {
            MediaType mediaType = requestBody.getContent().remove(org.springframework.http.MediaType.ALL_VALUE);
            if (mediaType != null) {
                requestBody.getContent().addMediaType(org.springframework.http.MediaType.APPLICATION_JSON_VALUE, mediaType);
            }
        }
    }

    private void processApiResponses(ApiResponses apiResponses) {
        if (apiResponses != null) {
            apiResponses.values().stream()
                    .filter(apiResponse -> apiResponse.getContent() != null)
                    .forEach(apiResponse -> {
                        MediaType mediaType = apiResponse.getContent().remove(org.springframework.http.MediaType.ALL_VALUE);
                        if (mediaType != null) {
                            apiResponse.getContent().addMediaType(org.springframework.http.MediaType.APPLICATION_JSON_VALUE, mediaType);
                        }
                    });
        }
    }
}
