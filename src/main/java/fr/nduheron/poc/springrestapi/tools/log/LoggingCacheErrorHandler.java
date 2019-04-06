package fr.nduheron.poc.springrestapi.tools.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;

public class LoggingCacheErrorHandler implements org.springframework.cache.interceptor.CacheErrorHandler {
    private static final Logger logger = LoggerFactory.getLogger(LoggingCacheErrorHandler.class);


    @Override
    public void handleCacheGetError(RuntimeException exception, Cache cache, Object key) {
        logger.warn("Erreur lors de la récupération de l'élément {} dans le cache {}", key, cache.getName(), exception);
    }

    @Override
    public void handleCachePutError(RuntimeException exception, Cache cache, Object key, Object value) {
        logger.warn("Erreur lors de l'alimentation de l'élément {} dans le cache {}", key, cache.getName(), exception);
    }

    @Override
    public void handleCacheEvictError(RuntimeException exception, Cache cache, Object key) {
        throw exception;
    }

    @Override
    public void handleCacheClearError(RuntimeException exception, Cache cache) {
        throw exception;
    }
}
