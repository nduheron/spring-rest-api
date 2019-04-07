package fr.nduheron.poc.springrestapi.tools.cache;

import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URI;
import java.util.Map;

/**
 * Generates an {@code ETag} value based on the
 * content on the response. This ETag is compared to the {@code If-None-Match}
 * header of the request. If these headers are equal, the response content is
 * not sent, but rather a {@code 304 "Not Modified"} status instead.
 */
public class EtagEvictInterceptor extends HandlerInterceptorAdapter {

    private CacheManager cacheManager;

    public EtagEvictInterceptor(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) throws Exception {

        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            // Test if the controller-method is annotated with @EtagEvict
            EtagEvict filter = handlerMethod.getMethod().getAnnotation(EtagEvict.class);
            if (filter != null && HttpStatus.valueOf(response.getStatus()).is2xxSuccessful()) {
                Cache cache = cacheManager.getCache(filter.cache());
                URI uri = new URI(request.getRequestURI());
                cache.evict(uri.toString().replaceAll("/$", StringUtils.EMPTY));
                if (filter.evictParentResource()) {
                    uri = uri.getPath().endsWith("/") ? uri.resolve("..") : uri.resolve(".");
                    cache.evict(uri.toString().replaceAll("/$", StringUtils.EMPTY));
                }
                if (filter.evictChildResources()) {
                    ((Map<String, Object>) cache.getNativeCache()).entrySet().removeIf(e -> e.getKey().startsWith(request.getRequestURI()));
                }

            }
        }
    }


}
