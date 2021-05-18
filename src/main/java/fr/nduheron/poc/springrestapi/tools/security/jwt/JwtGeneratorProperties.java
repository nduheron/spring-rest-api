package fr.nduheron.poc.springrestapi.tools.security.jwt;


import org.springframework.core.io.Resource;

import javax.validation.constraints.NotNull;

public class JwtGeneratorProperties {
    private long duration;
    private String issuer;
    @NotNull
    private Resource privateKey;

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public Resource getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(Resource privateKey) {
        this.privateKey = privateKey;
    }
}
