package fr.nduheron.poc.springrestapi.tools.security.autoconfigure;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.JWK;
import fr.nduheron.poc.springrestapi.tools.security.jwt.JwtDecoderProperties;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.stream.Collectors;

@Configuration
public class JwtDecoderAutoConfiguration {

    @Bean
    @ConfigurationProperties("security.jwt")
    @ConditionalOnProperty("security.jwt.publicKey")
    @ConditionalOnMissingBean
    protected JwtDecoderProperties jwtDecoderProperties() {
        return new JwtDecoderProperties();
    }

    @Bean
    @ConditionalOnBean(JwtDecoderProperties.class)
    @ConditionalOnMissingBean
    protected JwtDecoder jwtDecoder() {
        JwtDecoderProperties properties = jwtDecoderProperties();
        try (
                InputStreamReader inputStreamReader = new InputStreamReader(properties.getPublicKey().getInputStream());
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader)
        ) {
            JWK jwk = JWK.parseFromPEMEncodedX509Cert(bufferedReader.lines().parallel().collect(Collectors.joining(System.lineSeparator())));
            NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withPublicKey(jwk.toRSAKey().toRSAPublicKey()).build();

            if (properties.getIssuer() != null) {
                jwtDecoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(Arrays.asList(
                        new JwtTimestampValidator(),
                        new JwtIssuerValidator(properties.getIssuer())
                )));
            } else {
                jwtDecoder.setJwtValidator(new JwtTimestampValidator());
            }
            return jwtDecoder;
        } catch (IOException | JOSEException e) {
            throw new IllegalStateException("An error occured reading private key", e);
        }
    }

}
