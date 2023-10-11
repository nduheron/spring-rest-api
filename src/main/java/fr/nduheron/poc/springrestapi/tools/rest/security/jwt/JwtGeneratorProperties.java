package fr.nduheron.poc.springrestapi.tools.rest.security.jwt;


import com.nimbusds.jose.jwk.RSAKey;

import javax.validation.constraints.NotNull;
import java.time.Duration;

public class JwtGeneratorProperties {
    private Duration duration;
    private String issuer;
    @NotNull
    private RSAKey privateKey;

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public RSAKey getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(RSAKey privateKey) {
        this.privateKey = privateKey;
    }
}
