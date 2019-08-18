package fr.nduheron.poc.springrestapi.config;

import fr.nduheron.poc.springrestapi.tools.cache.EtagEvictInterceptor;
import fr.nduheron.poc.springrestapi.tools.cache.EtagInterceptor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.format.Formatter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Configuration de l'API Rest
 */
@Configuration
public class RestConfiguration implements WebMvcConfigurer {

    @Autowired(required = false)
    private CacheManager cacheManager;

    @Autowired
    private Environment env;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        if (env.containsProperty("spring.hazelcast.config")) {
            registry.addInterceptor(new EtagInterceptor(cacheManager));
            registry.addInterceptor(new EtagEvictInterceptor(cacheManager));
        }
    }

    /**
     * Permet d'avoir un objet {@link LocalDate} en paramètre d'une API Rest
     */
    @Bean
    Formatter<LocalDate> localDateFormatter() {
        return new Formatter<LocalDate>() {
            @Override
            public LocalDate parse(String text, Locale locale) {
                return StringUtils.isBlank(text) ? null : LocalDate.parse(text, DateTimeFormatter.ISO_DATE);
            }

            @Override
            public String print(LocalDate object, Locale locale) {
                return (object == null) ? null : DateTimeFormatter.ISO_DATE.format(object);
            }
        };
    }

    /**
     * Permet d'avoir un objet {@link LocalDateTime} en paramètre d'une API Rest
     */
    @Bean
    Formatter<LocalDateTime> localDateTimeFormatter() {
        return new Formatter<LocalDateTime>() {
            @Override
            public LocalDateTime parse(String text, Locale locale) {
                return StringUtils.isBlank(text) ? null : LocalDateTime.parse(text, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            }

            @Override
            public String print(LocalDateTime object, Locale locale) {
                return DateTimeFormatter.ISO_DATE.format(object);
            }
        };
    }

}
