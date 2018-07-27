package fr.nduheron.poc.springrestapi.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;

import fr.nduheron.poc.springrestapi.tools.log.ApiLoggingFilter;

/**
 * Configuration des filtres HTTP
 *
 */
@Configuration
@ConditionalOnProperty(name = "log.filter.enable", havingValue = "true")
public class FiltersConfiguration {

	@Value("${log.filter.excludePaths:}")
	private String[] excludePaths ;
	@Value("${log.filter.obfuscateParams:}")
	private String[] obfuscateParams;
	
	@Autowired
	private ObjectMapper mapper;

	@Bean
	FilterRegistrationBean<ApiLoggingFilter> loggingFilterRegistration() {
		FilterRegistrationBean<ApiLoggingFilter> registration = new FilterRegistrationBean<>();
		registration.setFilter(new ApiLoggingFilter(mapper, Lists.newArrayList(excludePaths),  Lists.newArrayList(obfuscateParams)));
		registration.setName(ApiLoggingFilter.class.getSimpleName());
		// Le filtre doit se lancer avant celui de la sécurité pour pouvoir logguer le
		// bon code retour HTTP
		registration.setOrder(SecurityProperties.DEFAULT_FILTER_ORDER - 1);
		return registration;
	}

}
