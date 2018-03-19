package fr.nduheron.poc.springrestapi.config;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.mail.javamail.JavaMailSender;

@Configuration
public class MockConfiguration {

	@Bean
	@Primary
	public JavaMailSender buildJavaMailSender() {
		return Mockito.mock(JavaMailSender.class);
	}
}
