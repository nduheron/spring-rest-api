package fr.nduheron.poc.springrestapi.tools.security;

import fr.nduheron.poc.springrestapi.tools.security.jwt.JwtTokenAuthenticationProcessingFilter;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;

public class WebSecurityConfigurer extends WebSecurityConfigurerAdapter {
    private final AuthenticationProvider jwtAuthenticationProvider;
    private final SecurityMatcher matcher;

    public WebSecurityConfigurer(AuthenticationProvider jwtAuthenticationProvider, SecurityMatcher matcher) {
        this.jwtAuthenticationProvider = jwtAuthenticationProvider;
        this.matcher = matcher;
    }

    protected JwtTokenAuthenticationProcessingFilter buildJwtTokenAuthenticationProcessingFilter() throws Exception {
        JwtTokenAuthenticationProcessingFilter filter = new JwtTokenAuthenticationProcessingFilter(matcher, jwtAuthenticationProvider);
        filter.setAuthenticationManager(authenticationManager());
        return filter;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(jwtAuthenticationProvider);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .cors().disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .formLogin().disable()
                .httpBasic().disable()
                .exceptionHandling().authenticationEntryPoint(new RestAuthenticationEntryPoint()).and()
                .addFilterBefore(buildJwtTokenAuthenticationProcessingFilter(), FilterSecurityInterceptor.class)
                .headers()
                .cacheControl().disable()
                .frameOptions().disable();
    }
}
