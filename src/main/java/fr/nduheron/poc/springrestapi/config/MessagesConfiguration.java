package fr.nduheron.poc.springrestapi.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

/**
 * Configuration des messages pour l'internationalisation
 *
 */
@Configuration
public class MessagesConfiguration {

	@Bean
	MessageSource messageSource() {
		ReloadableResourceBundleMessageSource source = new ReloadableResourceBundleMessageSource();
		source.setBasenames("classpath:/i18n/messages");
		source.setUseCodeAsDefaultMessage(false);
		return source;
	}
}
