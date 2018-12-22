package fr.nduheron.poc.springrestapi.tools.actuator.monitoring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.metrics.cache.CacheMetricsRegistrar;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@ConditionalOnProperty({"management.metrics.caches", "spring.hazelcast.config"})
public class HazelcastMetricsRegistrar {

    @Value("${management.metrics.caches}")
    private String[] caches;
    @Autowired
    private CacheMetricsRegistrar cacheMetricsRegistrar;
    @Autowired
    private CacheManager cacheManager;

    @PostConstruct
    private void register() {
        for (String cacheNames : caches) {
            Cache cache = this.cacheManager.getCache(cacheNames);
            this.cacheMetricsRegistrar.bindCacheToRegistry(cache);
        }
    }
}
