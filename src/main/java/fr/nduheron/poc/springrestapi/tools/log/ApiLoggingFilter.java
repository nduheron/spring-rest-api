package fr.nduheron.poc.springrestapi.tools.log;

import com.google.common.base.Predicate;
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
import java.util.*;

import static com.google.common.base.Predicates.*;

/**
 * Filtre permettant de logger les requêtes et réponses de tous les appels REST.
 */
public class ApiLoggingFilter extends OncePerRequestFilter {


    private static final Logger LOG = LoggerFactory.getLogger(ApiLoggingFilter.class);
    private static final String UNKNOWN = "<unknown>";
    private static final String PATTERN_REPLACER = "\"$1\":\"xxxxx\"";
    private static final String OBFUSCATE_VALUE = "xxxxx";

    private List<String> obfuscateParams;
    private List<String> obfuscateHeader;
    private Predicate<String> noFilterPathMatcher;

    public ApiLoggingFilter(String logFilterPath, List<String> logExcludePaths,
                            List<String> obfuscateParams, List<String> obfuscateHeader) {
        this.obfuscateParams = obfuscateParams;
        this.obfuscateHeader = obfuscateHeader;

        Predicate<String> includePathMatcher = not(new AntPathPredicate(logFilterPath));
        List<Predicate<String>> excludesAntMatchers = new ArrayList<>();
        for (String exclude : logExcludePaths) {
            excludesAntMatchers.add(new AntPathPredicate(exclude));
        }
        noFilterPathMatcher = or(includePathMatcher, and(excludesAntMatchers));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        ContentCachingRequestWrapper requestToUse = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper responseToUse = new ContentCachingResponseWrapper(response);
        long debut = System.currentTimeMillis();
        try {
            filterChain.doFilter(requestToUse, responseToUse);
        } finally {
            long fin = System.currentTimeMillis();
            if (response.getStatus() >= 500) {
                LOG.error(buildHttpModel(requestToUse, responseToUse, fin - debut).toString());
            } else if (response.getStatus() >= 400) {
                LOG.warn(buildHttpModel(requestToUse, responseToUse, fin - debut).toString());
            } else if (LOG.isInfoEnabled()) {
                LOG.info(buildHttpModel(requestToUse, responseToUse, fin - debut).toString());
            }
            responseToUse.copyBodyToResponse();
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

        if (LOG.isDebugEnabled() || response.getStatus() >= 400) {
            httpModel.setRequestContent(getRequestContent(request));
        }

        if (LOG.isDebugEnabled()) {
            httpModel.setResponseContent(getResponseContent(response));
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
        for (String param : obfuscateParams) {
            res = res.replaceAll("(?i)\"(\\w*(?:" + param + "))\"\\s*:\\s*\".+?\"", PATTERN_REPLACER);
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
            requestHeaders.put(key, obfuscateHeader.contains(key) ? OBFUSCATE_VALUE : request.getHeader(key));
        }
        return requestHeaders;
    }

    private Map<String, String> getResponseHeaders(final HttpServletResponse response) {
        Map<String, String> responseHeaders = new HashMap<>();
        for (String key : response.getHeaderNames()) {
            responseHeaders.put(key, obfuscateHeader.contains(key) ? OBFUSCATE_VALUE : response.getHeader(key));
        }
        return responseHeaders;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return noFilterPathMatcher.apply(request.getRequestURI());
    }

}
