package fr.nduheron.poc.springrestapi.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration des caches.
 *
 */
@Configuration
@EnableCaching
public class CacheConfiguration {

//	@Bean
//	CacheManager cacheManager() {
//		return new ConcurrentMapCacheManager();
//	}
}
