package fr.nduheron.poc.springrestapi.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import fr.nduheron.poc.springrestapi.dto.UserDto;
import fr.nduheron.poc.springrestapi.tools.exception.TechnicalException;
import fr.nduheron.poc.springrestapi.tools.security.jwt.JwtAuthenticationToken;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;

/**
 * Construit l'{@link Authentication} à partir du token JWT
 */
@Component
public class JwtAuthenticationProvider implements AuthenticationProvider {

    private static final String PREFIX_ROLE = "ROLE_";
    private static final String USER_INFOS = "userInfos";

    @Value("${security.token.secret}")
    private String tokenSecret;
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public Authentication authenticate(Authentication authentication) {
        UserDto user = extractToken((String) authentication.getCredentials());

        // on ajoute l'utilisateur au contexte SLF4J
        MDC.put("user", user.getLogin());

        return new JwtAuthenticationToken(user,
                Lists.newArrayList(new SimpleGrantedAuthority(PREFIX_ROLE + user.getRole().name())));
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JwtAuthenticationToken.class.isAssignableFrom(authentication);
    }

    /**
     * Extrait les infos utilisateurs du token.
     */
    private UserDto extractToken(String tokenString) {
        try {
            Claims claims = Jwts.parser().setSigningKey(DatatypeConverter.parseBase64Binary(tokenSecret))
                    .parseClaimsJws(tokenString).getBody();
            return objectMapper.readValue((String) claims.get(USER_INFOS), UserDto.class);
        } catch (ExpiredJwtException e) {
            throw new CredentialsExpiredException("La session utilisateur a expirée.", e);
        } catch (IOException e) {
            throw new TechnicalException("Erreur lors de la lecture du token JWT", e);
        }
    }
}
