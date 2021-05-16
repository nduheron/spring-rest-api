package fr.nduheron.poc.springrestapi.tools.log;

import fr.nduheron.poc.springrestapi.tools.AntPathPredicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import org.springframework.web.util.WebUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * Filtre permettant de logger les requêtes et réponses de tous les appels REST.
 */
public class ApiLoggingFilter extends OncePerRequestFilter {


    private static final Logger LOG = LoggerFactory.getLogger(ApiLoggingFilter.class);
    private static final String UNKNOWN = "<unknown>";
    private static final String PATTERN_REPLACER = "\"$1\":\"xxxxx\"";
    private static final String OBFUSCATE_VALUE = "xxxxx";

    private final LogProperties properties;
    private Predicate<String> noFilterPathMatcher;

    public ApiLoggingFilter(LogProperties properties) {
        this.properties = properties;

        noFilterPathMatcher = isNotBlank(properties.getPath()) ? new AntPathPredicate(properties.getPath()).negate() : s -> true;

        properties.getExcludePaths().stream()
                .map(antPattern -> (Predicate<String>) new AntPathPredicate(antPattern))
                .reduce(Predicate::or)
                .ifPresent(excludePath -> noFilterPathMatcher = noFilterPathMatcher.or(excludePath));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        HttpServletRequest requestToUse = properties.isBodyEnabled() ? new ContentCachingRequestWrapper(request) : request;
        HttpServletResponse responseToUse = properties.isBodyEnabled() ? new ContentCachingResponseWrapper(response) : response;
        long debut = System.currentTimeMillis();
        try {
            filterChain.doFilter(requestToUse, responseToUse);
        } finally {
            long fin = System.currentTimeMillis();
            if (response.getStatus() >= 500 && LOG.isErrorEnabled()) {
                LOG.error(buildHttpModel(requestToUse, responseToUse, fin - debut).toString());
            } else if (response.getStatus() >= 400 && LOG.isWarnEnabled()) {
                LOG.warn(buildHttpModel(requestToUse, responseToUse, fin - debut).toString());
            } else if (LOG.isInfoEnabled()) {
                LOG.info(buildHttpModel(requestToUse, responseToUse, fin - debut).toString());
            }
            if (properties.isBodyEnabled()) {
                ((ContentCachingResponseWrapper) responseToUse).copyBodyToResponse();
            }
        }
    }

    /**
     * Construite le message de log.
     *
     * @return le message à logguer
     */
    private HttpModel buildHttpModel(final HttpServletRequest request, final HttpServletResponse response, long time) {
        HttpModel httpModel = new HttpModel();

        httpModel.setMethod(request.getMethod());
        httpModel.setPath(request.getRequestURI());
        httpModel.setQueryString(request.getQueryString());
        httpModel.setStatusCode(response.getStatus());
        httpModel.setDurationInMs(time);

        if (LOG.isTraceEnabled()) {
            httpModel.setRequestHeaders(getRequestHeaders(request));
            httpModel.setResponseHeaders(getResponseHeaders(response));
        }

        if (properties.isBodyEnabled()) {
            if (LOG.isDebugEnabled() || response.getStatus() >= 400) {
                httpModel.setRequestContent(getRequestContent(request));
            }

            if (LOG.isDebugEnabled()) {
                httpModel.setResponseContent(getResponseContent(response));
            }
        }

        return httpModel;
    }

    private String getRequestContent(final HttpServletRequest request) {
        ContentCachingRequestWrapper wrapper = WebUtils.getNativeRequest(request, ContentCachingRequestWrapper.class);
        if (wrapper != null) {
            byte[] buf = wrapper.getContentAsByteArray();
            if (buf.length > 0) {
                try {
                    String str = new String(buf, 0, buf.length, wrapper.getCharacterEncoding());
                    return obfuscate(str);
                } catch (UnsupportedEncodingException ex) {
                    LOG.debug("Erreur lors de la récupération du body de la requête", ex);
                    return UNKNOWN;
                }
            }
        }
        return null;
    }

    private String obfuscate(String str) {
        String res = str;
        for (String param : properties.getObfuscateParams()) {
            res = res.replaceAll("(?i)\"(\\w*(" + param + "))\"\\s*:\\s*\".+?\"", PATTERN_REPLACER);
        }
        return res;
    }

    private String getResponseContent(final HttpServletResponse response) {
        ContentCachingResponseWrapper responseWrapper = WebUtils.getNativeResponse(response,
                ContentCachingResponseWrapper.class);
        if (responseWrapper != null) {
            byte[] buf = responseWrapper.getContentAsByteArray();
            if (buf.length > 0) {
                try {
                    String str = new String(buf, 0, buf.length, responseWrapper.getCharacterEncoding());
                    return obfuscate(str);
                } catch (UnsupportedEncodingException ex) {
                    LOG.debug("Erreur lors de la récupération du body de la réponse", ex);
                    return UNKNOWN;
                }
            }
        }
        return null;
    }

    private Map<String, String> getRequestHeaders(final HttpServletRequest request) {
        Enumeration<String> headerNames = request.getHeaderNames();
        Map<String, String> requestHeaders = new HashMap<>();
        while (headerNames.hasMoreElements()) {
            String key = headerNames.nextElement();
            requestHeaders.put(key, properties.getObfuscateHeader().contains(key) ? OBFUSCATE_VALUE : request.getHeader(key));
        }
        return requestHeaders;
    }

    private Map<String, String> getResponseHeaders(final HttpServletResponse response) {
        Map<String, String> responseHeaders = new HashMap<>();
        for (String key : response.getHeaderNames()) {
            responseHeaders.put(key, properties.getObfuscateHeader().contains(key) ? OBFUSCATE_VALUE : response.getHeader(key));
        }
        return responseHeaders;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return noFilterPathMatcher.test(request.getRequestURI());
    }

}
