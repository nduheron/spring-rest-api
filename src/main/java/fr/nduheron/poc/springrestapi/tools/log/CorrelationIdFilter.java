package fr.nduheron.poc.springrestapi.tools.log;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

/**
 * Filtre HTTP permettant d'ajouter le correlation ID du header dans le contexte slf4j
 */
public class CorrelationIdFilter extends OncePerRequestFilter {

    public static final String CORRELATION_ID_HEADER_NAME = "X-Correlation-ID";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestCid = StringUtils.isBlank(request.getHeader(CORRELATION_ID_HEADER_NAME)) ? UUID.randomUUID().toString() : request.getHeader(CORRELATION_ID_HEADER_NAME);
        // on met l'id de corrélation dans le contexte slf4j
        MDC.put(CORRELATION_ID_HEADER_NAME, requestCid);
        response.addHeader(CORRELATION_ID_HEADER_NAME, requestCid);
        try {
            filterChain.doFilter(request, response);
        } finally {
            // on nettoie le contexte slf4j à la fin de la request
            MDC.remove(CORRELATION_ID_HEADER_NAME);
        }
    }
}
