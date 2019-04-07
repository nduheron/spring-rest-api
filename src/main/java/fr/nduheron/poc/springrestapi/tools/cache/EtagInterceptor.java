package fr.nduheron.poc.springrestapi.tools.cache;

import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.CacheManager;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.util.DigestUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
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
public class EtagInterceptor extends HandlerInterceptorAdapter {

    private CacheManager cacheManager;

    public EtagInterceptor(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            // Test if the controller-method is annotated with @Etag
            Etag filter = handlerMethod.getMethod().getAnnotation(Etag.class);
            if (filter != null && StringUtils.isNotEmpty(filter.cache())) {
                Optional<String> requestEtag = Optional.ofNullable(request.getHeader(HttpHeaders.IF_NONE_MATCH));
                Optional<String> responseEtag = Optional.ofNullable(cacheManager.getCache(filter.cache()).get(request.getRequestURI().replaceAll("/$", StringUtils.EMPTY), String.class));
                if (requestEtag.isPresent() && responseEtag.isPresent() && requestEtag.equals(responseEtag)) {
                    response.setStatus(HttpStatus.NOT_MODIFIED.value());
                    return false;
                }
            }
        }
        return true;
    }


    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) throws Exception {

        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            // Test if the controller-method is annotated with @Etag
            Etag filter = handlerMethod.getMethod().getAnnotation(Etag.class);
            if (filter != null && HttpStatus.OK.value() == response.getStatus()) {
                ContentCachingResponseWrapper responseWrapper = WebUtils.getNativeResponse(response, ContentCachingResponseWrapper.class);
                String responseEtag = generateETagHeaderValue(responseWrapper.getContentInputStream());
                cacheManager.getCache(filter.cache()).put(request.getRequestURI().replaceAll("/$", StringUtils.EMPTY), responseEtag);
                Optional<String> requestEtag = Optional.ofNullable(request.getHeader(HttpHeaders.IF_NONE_MATCH));
                if (requestEtag.isPresent() && requestEtag.get().equals(responseEtag)) {
                    response.setStatus(HttpStatus.NOT_MODIFIED.value());
                } else {
                    String headerValue = CacheControl.maxAge(filter.maxAge(), TimeUnit.SECONDS)
                            .getHeaderValue();
                    response.addHeader(HttpHeaders.CACHE_CONTROL, headerValue);
                    response.setHeader(HttpHeaders.ETAG, responseEtag);
                    responseWrapper.copyBodyToResponse();
                }
            }
        }

    }


    protected String generateETagHeaderValue(InputStream inputStream) throws IOException {
        StringBuilder builder = new StringBuilder(37);
        builder.append("\"0");
        DigestUtils.appendMd5DigestAsHex(inputStream, builder);
        builder.append('"');
        return builder.toString();
    }
}
