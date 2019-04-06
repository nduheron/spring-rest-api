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
import java.util.regex.Pattern;

/**
 * Filtre HTTP permettant d'ajouter le correlation ID du header dans le contexte slf4j
 */
public class CorrelationIdFilter extends OncePerRequestFilter {

    public static final String CORRELATION_ID_HEADER_NAME = "X-Correlation-ID";
    private static final Pattern UUID_PATTERN = Pattern.compile("([a-f0-9]{8}(-[a-f0-9]{4}){3}-[a-f0-9]{11})");
    private static final String CLIENT_IP = "client_ip";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader(CORRELATION_ID_HEADER_NAME);
        String requestCid = StringUtils.isBlank(header) || !UUID_PATTERN.matcher(header).matches() ? UUID.randomUUID().toString() : header;
        // on met l'id de corrélation dans le contexte slf4j
        MDC.put(CORRELATION_ID_HEADER_NAME, requestCid);
        MDC.put(CLIENT_IP, request.getRemoteAddr());
        response.addHeader(CORRELATION_ID_HEADER_NAME, requestCid);

        try {
            filterChain.doFilter(request, response);
        } finally {
            // on nettoie le contexte slf4j à la fin de la request
            MDC.clear();
        }
    }
}
