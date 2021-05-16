package fr.nduheron.poc.springrestapi.tools.cache;

import org.springframework.core.env.Environment;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.util.DigestUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.ContentCachingResponseWrapper;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Generates an {@code ETag} value based on the
 * content on the response. This ETag is compared to the {@code If-None-Match}
 * header of the request. If these headers are equal, the response content is
 * not sent, but rather a {@code 304 "Not Modified"} status instead.
 */
public class EtagInterceptor implements HandlerInterceptor {
    private final Environment environment;

    public EtagInterceptor(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) throws Exception {

        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            // Test if the controller-method is annotated with @Etag
            Etag filter = handlerMethod.getMethod().getAnnotation(Etag.class);
            if (filter == null) {
                response.setHeader(HttpHeaders.CACHE_CONTROL, getNoCacheHeader());
            } else if (HttpStatus.OK.value() == response.getStatus()) {
                ContentCachingResponseWrapper responseWrapper = WebUtils.getNativeResponse(response, ContentCachingResponseWrapper.class);
                if (responseWrapper != null) {
                    String responseEtag = generateETagHeaderValue(responseWrapper.getContentInputStream());
                    Optional<String> requestEtag = Optional.ofNullable(request.getHeader(HttpHeaders.IF_NONE_MATCH));
                    if (requestEtag.isPresent() && requestEtag.get().equals(responseEtag)) {
                        response.setStatus(HttpStatus.NOT_MODIFIED.value());
                        response.setHeader(HttpHeaders.CACHE_CONTROL, getMaxAgeCacheHeader(filter.maxAge()));
                    } else {
                        response.setHeader(HttpHeaders.CACHE_CONTROL, getMaxAgeCacheHeader(filter.maxAge()));
                        response.setHeader(HttpHeaders.ETAG, responseEtag);
                        responseWrapper.copyBodyToResponse();
                    }
                }
            }
        }
    }

    private String getNoCacheHeader() {
        return CacheControl.noStore().mustRevalidate().getHeaderValue();
    }

    private String getMaxAgeCacheHeader(String maxAgeProperty) {
        long maxAge = Long.parseLong(environment.resolvePlaceholders(maxAgeProperty));
        return CacheControl.maxAge(maxAge, TimeUnit.SECONDS).cachePrivate().getHeaderValue();
    }

    private String generateETagHeaderValue(InputStream inputStream) throws IOException {
        StringBuilder builder = new StringBuilder(37);
        builder.append("\"0");
        DigestUtils.appendMd5DigestAsHex(inputStream, builder);
        builder.append('"');
        return builder.toString();
    }
}
