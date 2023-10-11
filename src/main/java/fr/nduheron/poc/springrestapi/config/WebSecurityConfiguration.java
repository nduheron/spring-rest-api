package fr.nduheron.poc.springrestapi.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.nduheron.poc.springrestapi.tools.rest.security.jwt.JwtAuthenticationToken;
import fr.nduheron.poc.springrestapi.tools.rest.security.jwt.JwtAuthenticationTokenConverter;
import fr.nduheron.poc.springrestapi.user.dto.UserDto;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;

/**
 * Configuration de la sécurité
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, jsr250Enabled = true)
public class WebSecurityConfiguration {
    private static final String PREFIX_ROLE = "ROLE_";

    @Bean
    protected PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    protected JwtAuthenticationTokenConverter jwtAuthenticationTokenConverter(ObjectMapper mapper) {
        return jwt -> {
            UserDto user = mapper.convertValue(jwt.getClaims(), UserDto.class);
            return new JwtAuthenticationToken(user,
                    Collections.singletonList(new SimpleGrantedAuthority(PREFIX_ROLE + user.getRole().name())));
        };
    }
}
