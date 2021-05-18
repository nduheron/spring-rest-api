package fr.nduheron.poc.springrestapi.tools.security.jwt;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import fr.nduheron.poc.springrestapi.tools.exception.TechnicalException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;

public class JwtGenerator {
    private final ObjectMapper mapper;
    private final JwtGeneratorProperties properties;
    private final JWSHeader header;
    private final JWSSigner jwsSigner;

    public JwtGenerator(ObjectMapper mapper, JwtGeneratorProperties properties) {
        try {
            this.mapper = mapper;
            this.properties = properties;
            RSAKey rsaKey = buildRSAKey();
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
                    .expirationTime(Date.from(now.plusSeconds(properties.getDuration())));

            mapper.convertValue(claims, new TypeReference<Map<String, Object>>() {
            }).forEach(payloadBuilder::claim);

            SignedJWT signedJWT = new SignedJWT(header, payloadBuilder.build());
            signedJWT.sign(jwsSigner);
            return signedJWT.serialize();

        } catch (Exception e) {
            throw new TechnicalException("Erreur lors de la creation du JWT", e);
        }
    }

    public long getDuration() {
        return properties.getDuration();
    }

    public RSAKey buildRSAKey() {
        try (
                InputStreamReader inputStreamReader = new InputStreamReader(properties.getPrivateKey().getInputStream());
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader)
        ) {
            JWK jwk = JWK.parseFromPEMEncodedObjects(bufferedReader.lines().parallel().collect(Collectors.joining(System.lineSeparator())));
            return jwk.toRSAKey();
        } catch (IOException | JOSEException e) {
            throw new IllegalStateException("An error occured reading private key", e);
        }
    }
}
