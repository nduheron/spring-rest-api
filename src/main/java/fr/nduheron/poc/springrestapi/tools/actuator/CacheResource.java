package fr.nduheron.poc.springrestapi.tools.actuator;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Conditional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import fr.nduheron.poc.springrestapi.tools.actuator.autoconfiguration.ActuatorCondition;
import fr.nduheron.poc.springrestapi.tools.exception.NotFoundException;
import io.swagger.annotations.ApiOperation;

/**
 * Controller permettant de gérer les caches Spring
 */
@RestController
// on ajoute la resource à actuator
@RequestMapping("${management.endpoints.web.base-path:/actuator}/caches")
// La ressource est active seulement si un manager de cache est configuré
@ConditionalOnBean(CacheManager.class)
@Conditional(ActuatorCondition.class)
public class CacheResource {

	@Autowired
	private CacheManager cacheManager;

	@ApiOperation(value = "Rechercher un cache")
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public Object find(@PathVariable("id") final String id) throws NotFoundException {
		final Cache cache = this.cacheManager.getCache(id);
		if (cache == null) {
			throw new NotFoundException(String.format("Le cache %s n'existe pas.", id));
		}
		return cache.getNativeCache();
	}

	@ApiOperation(value = "Flush l'ensemble des caches")
	@RequestMapping(method = RequestMethod.DELETE)
	public void flush() {
		findAll().forEach(id -> {
			flush(id);
		});
	}

	@ApiOperation(value = "Récupérer tous les caches")
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public Collection<String> findAll() {
		return this.cacheManager.getCacheNames();
	}

	@ApiOperation(value = "Flush un cache donné")
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public void flush(@PathVariable("id") final String id) {
		final Cache cache = this.cacheManager.getCache(id);
		if (cache != null) {
			cache.clear();
		}
	}

}
