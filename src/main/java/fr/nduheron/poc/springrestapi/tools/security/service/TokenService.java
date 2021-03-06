/**
 *
 */
package fr.nduheron.poc.springrestapi.tools.security.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.nduheron.poc.springrestapi.tools.exception.TechnicalException;
import fr.nduheron.poc.springrestapi.tools.security.domain.Token;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.stereotype.Service;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Service métier pour la gestion du jeton.
 *
 */
@Service
public class TokenService {

    private static final String USER_INFOS = "userInfos";

    @Value("${security.token.ttl}")
    private String tokenTTL;

    @Value("${security.token.secret}")
    private String tokenSecret;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Création du jeton avec les données utilisateurs en paramètre
     */
    public Token createToken(final Object userInfos) {
        try {
            // The JWT signature algorithm we will be using to sign the token
            SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

            long nowMillis = System.currentTimeMillis();
            Date now = new Date(nowMillis);

            // We will sign our JWT with our ApiKey secret
            byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(tokenSecret);
            Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

            // Let's set the JWT Claims
            JwtBuilder builder = Jwts.builder().setIssuedAt(now).signWith(signatureAlgorithm, signingKey);

            if (userInfos != null) {
                Map<String, Object> claims = new HashMap<>();
                claims.put(USER_INFOS, objectMapper.writeValueAsString(userInfos));
                builder.setClaims(claims);
            }
            // if it has been specified, let's add the expiration
            Long ttl = Long.parseLong(tokenTTL);
            long expMillis = nowMillis + ttl;
            Date exp = new Date(expMillis);
            builder.setExpiration(exp);

            return new Token(builder.compact(), ttl);
        } catch (JsonProcessingException e) {
            throw new TechnicalException("Erreur lors de la creation du token JWT", e);
        }
    }

    /**
     * Extrait les infos utilisateurs du token.
     */
    public <T> T extractToken(String tokenString, Class<T> type) {
        try {
            Claims claims = Jwts.parser().setSigningKey(DatatypeConverter.parseBase64Binary(tokenSecret))
                    .parseClaimsJws(tokenString).getBody();
            return objectMapper.readValue((String) claims.get(USER_INFOS), type);
        } catch (ExpiredJwtException e) {
            throw new CredentialsExpiredException("La session utilisateur a expirée.", e);
        } catch (IOException e) {
            throw new TechnicalException("Erreur lors de la lecture du token JWT", e);
        }
    }

}
