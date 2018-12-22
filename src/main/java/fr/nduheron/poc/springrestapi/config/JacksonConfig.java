package fr.nduheron.poc.springrestapi.config;

import com.fasterxml.jackson.module.afterburner.AfterburnerModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    /**
     * Ajoute AfterBurner à jackson pour améliorer les perfs de la sérialisation JSON
     */
    @Bean
    AfterburnerModule afterburnerModule() {
        return new AfterburnerModule();
    }
}
