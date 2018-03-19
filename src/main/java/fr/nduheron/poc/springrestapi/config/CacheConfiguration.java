package fr.nduheron.poc.springrestapi.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration des caches.
 *
 */
@Configuration
@EnableCaching
public class CacheConfiguration {

	@Bean
	CacheManager cacheManager() {
		return new ConcurrentMapCacheManager();
	}
}
