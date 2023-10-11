package fr.nduheron.poc.springrestapi.tools.rest.openapi;

import fr.nduheron.poc.springrestapi.tools.rest.errors.dto.AttributeErrorDto;
import fr.nduheron.poc.springrestapi.tools.rest.errors.dto.FunctionalErrorDto;
import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.core.converter.ResolvedSchema;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.SpringDocUtils;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.boot.info.BuildProperties;

import java.util.*;

import static java.util.Optional.ofNullable;

public class OpenAPIFactory implements FactoryBean<OpenAPI> {

    private final BuildProperties buildProperties;
    private final OpenApiProperties openApiProperties;
    private final Map<Class<?>, Class<?>> additionalSubstitutes = new HashMap<>();
    private List<Class<?>> additionalModels = List.of(AttributeErrorDto.class, FunctionalErrorDto.class);
    private final Map<String, SecurityScheme> securitySchemes = new HashMap<>();

    public OpenAPIFactory(BuildProperties buildProperties, OpenApiProperties openApiProperties) {
        this.buildProperties = buildProperties;
        this.openApiProperties = openApiProperties;
    }

    @Override
    public OpenAPI getObject() {
        OpenAPI openAPI = new OpenAPI()
                .info(apiInfo())
                .components(new Components().securitySchemes(securitySchemes));

        if (openApiProperties.getUrl() != null) {
            openAPI.setServers(Collections.singletonList(
                    new Server().url(openApiProperties.getUrl())
            ));
        }

        additionalModels.forEach(it -> {
            ResolvedSchema modelSchema = resolvedSchema(it);
            openAPI.schema(modelSchema.schema.getName(), modelSchema.schema);
        });

        additionalSubstitutes.forEach((key, value) -> SpringDocUtils.getConfig().replaceWithClass(key, value));

        return openAPI;
    }

    public void addSubstitute(Class<?> clazz, Class<?> with) {
        additionalSubstitutes.put(clazz, with);
    }

    public void addAdditionalModels(Class<?>... additionalModels) {
        this.additionalModels.addAll(Arrays.asList(additionalModels));
    }

    public void addSecurityScheme(String name, SecurityScheme securityScheme) {
        this.securitySchemes.put(name, securityScheme);
    }

    @Override
    public Class<?> getObjectType() {
        return OpenAPI.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    private Info apiInfo() {
        return new Info()
                .title(openApiProperties.getTitle())
                .description(openApiProperties.getDescription())
                .version(ofNullable(openApiProperties.getVersion()).orElse(buildProperties.getVersion()));
    }

    private ResolvedSchema resolvedSchema(Class<?> type) {
        return ModelConverters.getInstance().readAllAsResolvedSchema(new AnnotatedType(type));
    }
}
