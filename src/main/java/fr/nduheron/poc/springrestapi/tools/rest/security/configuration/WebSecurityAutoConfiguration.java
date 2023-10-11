package fr.nduheron.poc.springrestapi.tools.rest.security.configuration;

import fr.nduheron.poc.springrestapi.tools.rest.security.SecurityMatcher;
import fr.nduheron.poc.springrestapi.tools.rest.security.jwt.JwtTokenAuthenticationProcessingFilter;
import fr.nduheron.poc.springrestapi.tools.rest.security.jwt.LogAuthenticationFailureHandler;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.AllNestedConditions;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;


@Configuration
@AutoConfigureAfter({AuthenticationProviderAutoConfiguration.class, SecurityMatcherAutoConfiguration.class})
public class WebSecurityAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    @Conditional(WebSecurityAutoConfiguration.ConditionOnJwtAuthenticationProvider.class)
    protected SecurityFilterChain securityFilterChain(
            HttpSecurity httpSecurity,
            AuthenticationProvider jwtAuthenticationProvider,
            SecurityMatcher matcher
    ) throws Exception {
        httpSecurity
                .authenticationProvider(jwtAuthenticationProvider)
                .csrf().disable()
                .cors().disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .formLogin().disable()
                .httpBasic().disable()
                .addFilterBefore(new JwtTokenAuthenticationProcessingFilter(
                        matcher,
                        httpSecurity.getSharedObject(AuthenticationManager.class),
                        jwtAuthenticationProvider,
                        new LogAuthenticationFailureHandler()
                ), FilterSecurityInterceptor.class)
                .headers()
                .frameOptions().disable()
                .cacheControl().disable();
        return httpSecurity.build();
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
