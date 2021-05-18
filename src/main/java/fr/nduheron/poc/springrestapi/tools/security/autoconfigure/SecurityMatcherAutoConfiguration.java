package fr.nduheron.poc.springrestapi.tools.security.autoconfigure;

import fr.nduheron.poc.springrestapi.tools.security.SecurityConfigProperties;
import fr.nduheron.poc.springrestapi.tools.security.SecurityMatcher;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SecurityMatcherAutoConfiguration {

    @Bean
    @ConfigurationProperties("security.config")
    @ConditionalOnMissingBean
    protected SecurityConfigProperties securityConfigProperties() {
        return new SecurityConfigProperties();
    }

    @Bean
    @ConditionalOnBean(SecurityConfigProperties.class)
    @ConditionalOnMissingBean
    protected SecurityMatcher securityMatcher() {
        return new SecurityMatcher(securityConfigProperties());
    }
}
