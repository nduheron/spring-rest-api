package fr.nduheron.poc.springrestapi.tools.rest.cache;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration de l'API Rest
 */
@Configuration
public class EtagConfiguration implements WebMvcConfigurer {

    private final Environment env;

    public EtagConfiguration(Environment env) {
        this.env = env;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new EtagInterceptor(env));
    }

}
