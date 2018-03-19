package fr.nduheron.poc.springrestapi.tools.security.jwt;

import java.util.Collection;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {
	private static final long serialVersionUID = 2877954820905567501L;

	private String token;
	private Object userContext;

	public JwtAuthenticationToken(String token) {
		super(null);
		this.token = token;
		this.setAuthenticated(false);
	}

	public JwtAuthenticationToken(Object userContext, Collection<? extends GrantedAuthority> authorities) {
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
}
