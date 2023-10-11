package fr.nduheron.poc.springrestapi.tools.rest.security.jwt;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import fr.nduheron.poc.springrestapi.tools.exceptions.TechnicalException;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

public class JwtGenerator {
    private final ObjectMapper mapper;
    private final JwtGeneratorProperties properties;
    private final JWSHeader header;
    private final JWSSigner jwsSigner;

    public JwtGenerator(ObjectMapper mapper, JwtGeneratorProperties properties) {
        try {
            this.mapper = mapper;
            this.properties = properties;
            RSAKey rsaKey = properties.getPrivateKey();
            this.header = new JWSHeader.Builder(JWSAlgorithm.RS256)
                    .type(JOSEObjectType.JWT)
                    .keyID(rsaKey.getKeyID())
                    .build();
            this.jwsSigner = new RSASSASigner(rsaKey.toRSAPrivateKey());
        } catch (JOSEException e) {
            throw new IllegalStateException("Error building jwsSigner", e);
        }
    }

    public <T> String generateToken(T claims, String subject) {
        try {

            Instant now = Instant.now();

            JWTClaimsSet.Builder payloadBuilder = new JWTClaimsSet.Builder()
                    .issuer(properties.getIssuer())
                    .subject(subject)
                    .issueTime(Date.from(now))
                    .notBeforeTime(Date.from(now))
                    .expirationTime(Date.from(now.plus(properties.getDuration())));

            mapper.convertValue(claims, new TypeReference<Map<String, Object>>() {
            }).forEach(payloadBuilder::claim);

            SignedJWT signedJWT = new SignedJWT(header, payloadBuilder.build());
            signedJWT.sign(jwsSigner);
            return signedJWT.serialize();

        } catch (Exception e) {
            throw new TechnicalException("Error creating JWT", e);
        }
    }

    public Duration getDuration() {
        return properties.getDuration();
    }

}
