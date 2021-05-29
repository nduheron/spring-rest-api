package fr.nduheron.poc.springrestapi.tools.autoconfigure.rest;

import com.fasterxml.jackson.module.afterburner.AfterburnerModule;
import fr.nduheron.poc.springrestapi.tools.cache.EtagInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration de l'API Rest
 */
@Configuration
public class RestAutoConfiguration implements WebMvcConfigurer {

    @Autowired
    private Environment env;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new EtagInterceptor(env));
    }

    /**
     * Ajoute AfterBurner à jackson pour améliorer les perfs de la sérialisation JSON
     */
    @Bean
    @ConditionalOnClass(AfterburnerModule.class)
    AfterburnerModule afterburnerModule() {
        return new AfterburnerModule();
    }

}
