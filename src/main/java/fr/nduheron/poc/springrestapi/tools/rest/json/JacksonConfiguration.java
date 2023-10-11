package fr.nduheron.poc.springrestapi.tools.rest.json;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfiguration {

    @Bean
    JavaTimeModule javaTimeModule() {
        return new JavaTimeModule();
    }
}
