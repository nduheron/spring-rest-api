package fr.nduheron.poc.springrestapi.tools.autoconfigure.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.jwk.JWK;
import fr.nduheron.poc.springrestapi.tools.security.jwt.JwtGenerator;
import fr.nduheron.poc.springrestapi.tools.security.jwt.JwtGeneratorProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(JWK.class)
public class JwtGeneratorAutoConfiguration {

    @Bean
    @ConfigurationProperties("security.jwt")
    @ConditionalOnProperty("security.jwt.privateKey")
    @ConditionalOnMissingBean
    protected JwtGeneratorProperties jwtGeneratorProperties() {
        return new JwtGeneratorProperties();
    }

    @Bean
    @ConditionalOnBean(JwtGeneratorProperties.class)
    @ConditionalOnMissingBean
    protected JwtGenerator jwtGenerator(ObjectMapper objectMapper) {
        return new JwtGenerator(objectMapper, jwtGeneratorProperties());
    }

}
