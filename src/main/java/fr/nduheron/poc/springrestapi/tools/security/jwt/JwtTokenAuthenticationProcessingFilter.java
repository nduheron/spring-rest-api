package fr.nduheron.poc.springrestapi.tools.security.jwt;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;

import com.google.common.base.Strings;

/**
 * Filtre HTTP en charge de valider et décoder le Jeton d'authentification
 *
 */
public class JwtTokenAuthenticationProcessingFilter extends AbstractAuthenticationProcessingFilter {
	private static String HEADER_PREFIX = "Bearer ";

	public JwtTokenAuthenticationProcessingFilter(RequestMatcher matcher) {
		super(matcher);
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
		if (Strings.isNullOrEmpty(authorization)) {
			throw new BadCredentialsException("Vous devez être authentifié.");
		}

		if (authorization.length() < HEADER_PREFIX.length()) {
			throw new BadCredentialsException("Le jeton est invalide.");
		}

		String token = authorization.substring(HEADER_PREFIX.length(), authorization.length());
		return getAuthenticationManager().authenticate(new JwtAuthenticationToken(token));
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authResult) throws IOException, ServletException {
		SecurityContext context = SecurityContextHolder.createEmptyContext();
		context.setAuthentication(authResult);
		SecurityContextHolder.setContext(context);
		chain.doFilter(request, response);
	}

}
