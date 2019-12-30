package fr.nduheron.poc.springrestapi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@ConditionalOnProperty(value = "swagger-ui.enable", havingValue = "true")
public class SwaggerUiConfiguration implements WebMvcConfigurer {

    @Value("${swagger-ui.version}")
    private String swaggerUiVersion;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/swagger-ui/" + swaggerUiVersion + "/");
    }

}

