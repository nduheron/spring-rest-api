package fr.nduheron.poc.springrestapi.tools.rest.security.jwt;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.io.Serializable;
import java.util.Collection;
import java.util.Objects;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {
    private static final long serialVersionUID = 2877954820905567501L;

    private String token;
    private Serializable userContext;

    public JwtAuthenticationToken(String token) {
        super(null);
        this.token = token;
        this.setAuthenticated(false);
    }

    public JwtAuthenticationToken(Serializable userContext, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.eraseCredentials();
        this.userContext = userContext;
        super.setAuthenticated(true);
    }

    @Override
    public void setAuthenticated(boolean authenticated) {
        if (authenticated) {
            throw new IllegalArgumentException(
                    "Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead");
        }
        super.setAuthenticated(false);
    }

    @Override
    public Object getCredentials() {
        return token;
    }

    @Override
    public Object getPrincipal() {
        return this.userContext;
    }

    @Override
    public void eraseCredentials() {
        super.eraseCredentials();
        this.token = null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        JwtAuthenticationToken that = (JwtAuthenticationToken) o;
        return Objects.equals(token, that.token) && Objects.equals(userContext, that.userContext);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), token, userContext);
    }
}
