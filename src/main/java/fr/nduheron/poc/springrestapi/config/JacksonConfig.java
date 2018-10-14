package fr.nduheron.poc.springrestapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.module.afterburner.AfterburnerModule;

@Configuration
public class JacksonConfig {

	@Bean
	AfterburnerModule afterburnerModule() {
		return new AfterburnerModule();
	}
		
}
