package fr.nduheron.poc.springrestapi.tools.log;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration des filtres HTTP
 */
@Configuration
@ConditionalOnBean(LogProperties.class)
public class FiltersConfiguration {

    private static final int LOG_ORDER = SecurityProperties.DEFAULT_FILTER_ORDER - 1;
    private static final int CORRELATION_ORDER = SecurityProperties.DEFAULT_FILTER_ORDER - 2;

    @Bean
    FilterRegistrationBean<ApiLoggingFilter> loggingFilterRegistration(LogProperties logProperties) {
        FilterRegistrationBean<ApiLoggingFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new ApiLoggingFilter(logProperties));
        registration.setName(ApiLoggingFilter.class.getSimpleName());
        // Le filtre doit se lancer avant celui de la sécurité pour pouvoir logguer le
        // bon code retour HTTP
        registration.setOrder(LOG_ORDER);
        return registration;
    }

    @Bean
    FilterRegistrationBean<CorrelationIdFilter> correlationIdFilterRegistration() {
        FilterRegistrationBean<CorrelationIdFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new CorrelationIdFilter());
        registration.setName(CorrelationIdFilter.class.getSimpleName());
        // Le filtre doit se lancer avant celui de log pour avoir l'id dans les logs
        registration.setOrder(CORRELATION_ORDER);
        return registration;
    }
}
