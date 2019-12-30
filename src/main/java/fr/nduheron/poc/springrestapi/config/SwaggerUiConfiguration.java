package fr.nduheron.poc.springrestapi.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@ConditionalOnProperty(value = "swagger-ui.enable", havingValue = "true")
public class SwaggerUiConfiguration implements WebMvcConfigurer {
    private static final Logger logger = LoggerFactory.getLogger(SwaggerUiConfiguration.class);

    @Value("${swagger-ui.version}")
    private String swaggerUiVersion;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/swagger-ui/" + swaggerUiVersion + "/");

        logger.info("Swagger is available at: {}", contextPath + "/index.html");
    }

}

