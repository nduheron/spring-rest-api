package fr.nduheron.poc.springrestapi.tools.security.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import fr.nduheron.poc.springrestapi.tools.exception.TechnicalException;
import fr.nduheron.poc.springrestapi.tools.security.domain.Token;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.Instant;
import java.util.Date;

/**
 * Service métier pour la gestion du jeton.
 */
@Service
public class TokenService {

    private static final String USER_INFOS = "userInfos";

    private final Long tokenTTL;
    private final RSAKey key;
    private final ObjectMapper objectMapper;

    public TokenService(@Value("${security.token.ttl}") Long tokenTTL, @Value("${security.token.secret}") String tokenSecret, ObjectMapper objectMapper) throws JOSEException {
        this.tokenTTL = tokenTTL;
        this.key = new RSAKeyGenerator(2048)
                .keyID(tokenSecret)
                .generate();
        this.objectMapper = objectMapper;
    }

    /**
     * Création du jeton avec les données utilisateurs en paramètre
     */
    public Token createToken(final Object userInfos) {
        try {
            JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.RS256)
                    .type(JOSEObjectType.JWT)
                    .keyID(key.getKeyID())
                    .build();

            Instant now = Instant.now();

            JWTClaimsSet payload = new JWTClaimsSet.Builder()
                    .issueTime(Date.from(now))
                    .expirationTime(Date.from(now.plusSeconds(tokenTTL)))
                    .claim(USER_INFOS, objectMapper.writeValueAsString(userInfos))
                    .build();


            SignedJWT signedJWT = new SignedJWT(header, payload);
            signedJWT.sign(new RSASSASigner(key.toRSAPrivateKey()));
            String jwt = signedJWT.serialize();

            return new Token(jwt, tokenTTL);
        } catch (Exception e) {
            throw new TechnicalException("Erreur lors de la creation du token JWT", e);
        }
    }

    /**
     * Extrait les infos utilisateurs du token.
     */
    public <T> T extractToken(String tokenString, Class<T> type) {
        try {

            SignedJWT jwt = SignedJWT.parse(tokenString);
            boolean isValid = jwt.verify(new RSASSAVerifier(key.toRSAPublicKey()));
            if (!isValid) {
                throw new BadCredentialsException("Invalid JWT!!!");
            }
            return objectMapper.readValue((String) jwt.getJWTClaimsSet().getClaim(USER_INFOS), type);
        } catch (ParseException | JOSEException | JsonProcessingException e) {
            throw new TechnicalException("Erreur lors de la lecture du token JWT", e);
        }
    }

}
