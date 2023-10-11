package fr.nduheron.poc.springrestapi.tools.rest.security.configuration;

import com.nimbusds.jose.JOSEException;
import fr.nduheron.poc.springrestapi.tools.rest.security.jwt.JwtDecoderProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtIssuerValidator;
import org.springframework.security.oauth2.jwt.JwtTimestampValidator;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import java.util.Arrays;

@Configuration
public class JwtDecoderAutoConfiguration {

    @Bean
    @ConfigurationProperties("security.jwt")
    @ConditionalOnProperty("security.jwt.public-key")
    @ConditionalOnMissingBean
    protected JwtDecoderProperties jwtDecoderProperties() {
        return new JwtDecoderProperties();
    }

    @Bean
    @ConditionalOnBean(JwtDecoderProperties.class)
    @ConditionalOnMissingBean
    protected JwtDecoder jwtDecoder() throws JOSEException {
        JwtDecoderProperties properties = jwtDecoderProperties();

        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withPublicKey(properties.getPublicKey().toRSAPublicKey()).build();

        if (properties.getIssuer() != null) {
            jwtDecoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(Arrays.asList(
                    new JwtTimestampValidator(),
                    new JwtIssuerValidator(properties.getIssuer())
            )));
        } else {
            jwtDecoder.setJwtValidator(new JwtTimestampValidator());
        }
        return jwtDecoder;

    }

}
