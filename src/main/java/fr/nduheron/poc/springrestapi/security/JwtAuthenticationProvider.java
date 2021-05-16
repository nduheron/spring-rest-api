package fr.nduheron.poc.springrestapi.security;

import fr.nduheron.poc.springrestapi.tools.security.jwt.JwtAuthenticationToken;
import fr.nduheron.poc.springrestapi.tools.security.service.TokenService;
import fr.nduheron.poc.springrestapi.user.dto.UserDto;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Construit l'{@link Authentication} à partir du token JWT
 */
@Component
public class JwtAuthenticationProvider implements AuthenticationProvider {

    private static final String PREFIX_ROLE = "ROLE_";

    @Autowired
    private TokenService tokenService;

    @Override
    public Authentication authenticate(Authentication authentication) {
        UserDto user = tokenService.extractToken(((String) authentication.getCredentials()), UserDto.class);

        // on ajoute l'utilisateur au contexte SLF4J
        MDC.put("user", user.getLogin());

        return new JwtAuthenticationToken(user,
                Arrays.asList(new SimpleGrantedAuthority(PREFIX_ROLE + user.getRole().name())));
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JwtAuthenticationToken.class.isAssignableFrom(authentication);
    }

}
