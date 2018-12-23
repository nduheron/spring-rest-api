package fr.nduheron.poc.springrestapi.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import fr.nduheron.poc.springrestapi.tools.log.ApiLoggingFilter;
import fr.nduheron.poc.springrestapi.tools.log.CorrelationIdFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration des filtres HTTP
 */
@Configuration
@ConditionalOnProperty(name = "log.filter.path")
public class FiltersConfiguration {

    private static final int LOG_ORDER = SecurityProperties.DEFAULT_FILTER_ORDER - 1;
    private static final int CORRELATION_ORDER = SecurityProperties.DEFAULT_FILTER_ORDER - 2;

    @Autowired
    private ObjectMapper mapper;

    @Value("${log.filter.path}")
    private String logFilterPath;
    @Value("${log.filter.excludePaths:}")
    private String[] logExcludePaths;
    @Value("${log.filter.obfuscateParams:}")
    private String[] obfuscateParams;
    @Value("${log.filter.obfuscateHeader:}")
    private String[] obfuscateHeader;

    @Bean
    FilterRegistrationBean<ApiLoggingFilter> loggingFilterRegistration() {
        FilterRegistrationBean<ApiLoggingFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new ApiLoggingFilter(mapper, logFilterPath, Lists.newArrayList(logExcludePaths),
                Lists.newArrayList(obfuscateParams), Lists.newArrayList(obfuscateHeader)));
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
