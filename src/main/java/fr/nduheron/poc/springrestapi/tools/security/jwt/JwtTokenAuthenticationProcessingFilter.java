package fr.nduheron.poc.springrestapi.tools.security.jwt;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Filtre HTTP en charge de valider et décoder le Jeton d'authentification
 */
public class JwtTokenAuthenticationProcessingFilter extends AbstractAuthenticationProcessingFilter {
    public static final String HEADER_AUTHORIZATION = "Authorization";
    private static final String HEADER_PREFIX = "Bearer ";
    private AuthenticationProvider authenticationProvider;

    public JwtTokenAuthenticationProcessingFilter(RequestMatcher matcher, AuthenticationProvider authenticationProvider) {
        super(matcher);
        this.authenticationProvider = authenticationProvider;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {

        String authorization = request.getHeader(HEADER_AUTHORIZATION);
        if (StringUtils.isEmpty(authorization)) {
            throw new BadCredentialsException("Vous devez être authentifié.");
        }

        if (authorization.length() < HEADER_PREFIX.length()) {
            throw new BadCredentialsException("Le jeton est invalide.");
        }

        String token = authorization.substring(HEADER_PREFIX.length(), authorization.length());
        return authenticationProvider.authenticate(new JwtAuthenticationToken(token));
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
