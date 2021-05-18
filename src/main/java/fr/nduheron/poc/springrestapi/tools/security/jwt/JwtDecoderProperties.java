package fr.nduheron.poc.springrestapi.tools.security.jwt;


import org.springframework.core.io.Resource;

import javax.validation.constraints.NotNull;

public class JwtDecoderProperties {
    private String issuer;
    @NotNull
    private Resource publicKey;

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public Resource getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(Resource publicKey) {
        this.publicKey = publicKey;
    }
}
