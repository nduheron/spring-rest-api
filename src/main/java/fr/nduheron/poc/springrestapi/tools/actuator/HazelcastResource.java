package fr.nduheron.poc.springrestapi.tools.actuator;

import com.hazelcast.spring.cache.HazelcastCache;
import fr.nduheron.poc.springrestapi.tools.exception.NotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

/**
 * Controller permettant de gérer les caches Spring
 */
@RestController
// on ajoute la resource à actuator
@RequestMapping("${management.endpoints.web.base-path:/actuator}/caches")
// La ressource est active seulement si un manager de cache est configuré
@ConditionalOnProperty("spring.hazelcast.config")
@ConditionalOnClass(HazelcastCache.class)
@Tag(name = "Cache")
public class HazelcastResource {

    @Autowired
    private Environment env;

    @Autowired(required = false)
    private CacheManager cacheManager;

    @Operation(summary = "Rechercher un cache", hidden = true)
    @GetMapping("/{id}")
    public Object find(@PathVariable("id") final String id) throws NotFoundException {
        final Cache cache = getCacheManager().getCache(id);
        String[] disableCache = env.getProperty("cache.http.disable", String[].class);
        if (cache == null || ArrayUtils.contains(disableCache, id)) {
            throw new NotFoundException(String.format("Le cache %s n'existe pas.", id));
        }
        return cache.getNativeCache();
    }

    @Operation(summary = "Flush l'ensemble des caches", hidden = true)
    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void flush() throws NotFoundException {
        CacheManager lCacheManager = getCacheManager();
        lCacheManager.getCacheNames().forEach(id -> lCacheManager.getCache(id).clear());
    }

    @Operation(summary = "Récupérer tous les caches", hidden = true)
    @GetMapping
    public Collection<String> findAll() throws NotFoundException {
        return getCacheManager().getCacheNames();
    }

    @Operation(summary = "Flush un cache donné", hidden = true)
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void flush(@PathVariable("id") final String id) throws NotFoundException {
        final Cache cache = getCacheManager().getCache(id);
        if (cache != null) {
            cache.clear();
        }
    }

    private CacheManager getCacheManager() throws NotFoundException {
        if (cacheManager == null) {
            throw new NotFoundException("Aucun  cache de configuré!");
        }
        return this.cacheManager;
    }

}
