package fr.nduheron.poc.springrestapi.tools.security.jwt;

import org.slf4j.MDC;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;

/**
 * Construit l'{@link Authentication} à partir du token JWT
 */
public class JwtAuthenticationProvider implements AuthenticationProvider {

    private final JwtDecoder jwtDecoder;
    private final JwtAuthenticationTokenConverter converter;

    public JwtAuthenticationProvider(JwtDecoder jwtDecoder, JwtAuthenticationTokenConverter converter) {
        this.jwtDecoder = jwtDecoder;
        this.converter = converter;
    }

    @Override
    public Authentication authenticate(Authentication authentication) {
        Jwt jwt = jwtDecoder.decode((String) authentication.getCredentials());

        // on ajoute l'utilisateur au contexte SLF4J
        MDC.put("user", jwt.getSubject());

        return converter.convert(jwt);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JwtAuthenticationToken.class.isAssignableFrom(authentication);
    }

}
