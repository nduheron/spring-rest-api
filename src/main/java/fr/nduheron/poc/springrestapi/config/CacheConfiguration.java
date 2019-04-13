package fr.nduheron.poc.springrestapi.config;

import fr.nduheron.poc.springrestapi.tools.log.LoggingCacheErrorHandler;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration des caches.
 */
@Configuration
@EnableCaching(mode = AdviceMode.ASPECTJ)
public class CacheConfiguration extends CachingConfigurerSupport {

    @Bean
    @Override
    public CacheErrorHandler errorHandler() {
        return new LoggingCacheErrorHandler();
    }
}
