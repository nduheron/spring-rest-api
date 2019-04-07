package fr.nduheron.poc.springrestapi.tools.cache;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Objects;

/**
 * Generates an {@code ETag} value based on the
 * content on the response. This ETag is compared to the {@code If-None-Match}
 * header of the request. If these headers are equal, the response content is
 * not sent, but rather a {@code 304 "Not Modified"} status instead.
 */
public class EtagEvictInterceptor extends HandlerInterceptorAdapter {
    private static final Logger logger = LoggerFactory.getLogger(EtagEvictInterceptor.class);

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
                if (cache != null) {
                    String currentUri = request.getRequestURI().replaceAll("/$", StringUtils.EMPTY);

                    // supprime la route courante du cache
                    ((Map<String, Object>) cache.getNativeCache()).keySet().stream()
                            .filter(key -> Objects.equals(getPath(key), currentUri))
                            .forEach(cache::evict);

                    if (filter.evictParentResource()) {
                        String parentUri = new URI(currentUri).resolve(".").getPath().replaceAll("/$", StringUtils.EMPTY);
                        ((Map<String, Object>) cache.getNativeCache()).keySet().stream()
                                .filter(key -> Objects.equals(getPath(key), parentUri))
                                .forEach(cache::evict);
                    }

                    if (filter.evictChildResources()) {
                        ((Map<String, Object>) cache.getNativeCache()).keySet().stream()
                                .filter(key -> getPath(key).startsWith(currentUri))
                                .forEach(cache::evict);
                    }
                }
            }
        }
    }


    private String getPath(String url) {
        try {
            String path = new URI(url).getPath();
            return path;
        } catch (URISyntaxException e) {
            logger.warn("Erreur lors de la récupération du path", e);
            return null;
        }
    }

}
