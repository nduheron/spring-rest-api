package fr.nduheron.poc.springrestapi.tools.autoconfigure.security;

import fr.nduheron.poc.springrestapi.tools.security.SecurityMatcher;
import fr.nduheron.poc.springrestapi.tools.security.WebSecurityConfigurer;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.AllNestedConditions;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;


@Configuration
@AutoConfigureAfter({AuthenticationProviderAutoConfiguration.class, SecurityMatcherAutoConfiguration.class})
public class WebSecurityAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    @Conditional(WebSecurityAutoConfiguration.ConditionOnJwtAuthenticationProvider.class)
    protected WebSecurityConfigurer webSecurityConfigurer(AuthenticationProvider jwtAuthenticationProvider, SecurityMatcher matcher) {
        return new WebSecurityConfigurer(jwtAuthenticationProvider, matcher);
    }


    static class ConditionOnJwtAuthenticationProvider extends AllNestedConditions {

        ConditionOnJwtAuthenticationProvider() {
            super(ConfigurationPhase.REGISTER_BEAN);
        }

        @ConditionalOnBean(AuthenticationProvider.class)
        static class OnAuthenticationProvider {
        }

        @ConditionalOnBean(SecurityMatcher.class)
        static class OnSecurityMatcher {
        }
    }
}
