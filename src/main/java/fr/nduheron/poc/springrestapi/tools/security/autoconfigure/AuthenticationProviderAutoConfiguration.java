package fr.nduheron.poc.springrestapi.tools.security.autoconfigure;

import fr.nduheron.poc.springrestapi.tools.security.jwt.JwtAuthenticationProvider;
import fr.nduheron.poc.springrestapi.tools.security.jwt.JwtAuthenticationTokenConverter;
import org.springframework.boot.autoconfigure.condition.AllNestedConditions;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.oauth2.jwt.JwtDecoder;

@Configuration
public class AuthenticationProviderAutoConfiguration {

    @Bean
    @Conditional(ConditionOnJwtAuthenticationProvider.class)
    @ConditionalOnMissingBean
    protected AuthenticationProvider jwtAuthenticationProvider(JwtDecoder jwtDecoder, JwtAuthenticationTokenConverter converter) {
        return new JwtAuthenticationProvider(jwtDecoder, converter);
    }

    static class ConditionOnJwtAuthenticationProvider extends AllNestedConditions {

        ConditionOnJwtAuthenticationProvider() {
            super(ConfigurationPhase.PARSE_CONFIGURATION);
        }

        @ConditionalOnBean(JwtDecoder.class)
        static class OnJwtDecoder {
        }

        @ConditionalOnBean(JwtAuthenticationTokenConverter.class)
        static class OnJwtAuthenticationTokenConverter {
        }
    }
}
