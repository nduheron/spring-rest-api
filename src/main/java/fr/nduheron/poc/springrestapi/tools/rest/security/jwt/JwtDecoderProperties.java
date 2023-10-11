package fr.nduheron.poc.springrestapi.tools.rest.security.jwt;


import com.nimbusds.jose.jwk.RSAKey;

import javax.validation.constraints.NotNull;

public class JwtDecoderProperties {
    private String issuer;
    @NotNull
    private RSAKey publicKey;

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public RSAKey getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(RSAKey publicKey) {
        this.publicKey = publicKey;
    }
}
