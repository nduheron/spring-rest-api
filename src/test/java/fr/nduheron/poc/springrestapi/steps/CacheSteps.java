package fr.nduheron.poc.springrestapi.steps;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import fr.nduheron.poc.springrestapi.tools.AbstractCucumberSteps;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class CacheSteps extends AbstractCucumberSteps {

    private final CacheManager cacheManager;
    private final String cacheUrl;

    public CacheSteps(CacheManager cacheManager, @Value("${management.endpoints.web.base-path:/actuator}/caches") String cacheUrl) {
        this.cacheManager = cacheManager;
        this.cacheUrl = cacheUrl;
    }

    @Given("cache {string} with values:")
    public void item_in_cache(String cacheName, Map<String, Object> rows) {
        rows.entrySet().forEach(e -> cacheManager.getCache(cacheName).put(e.getKey(), e.getValue()));
    }

    @When("I search all caches")
    public void search_all_caches() {
        callCacheApi(StringUtils.EMPTY, HttpMethod.GET, null);
    }

    @When("I search cache {string}")
    public void search_cache(String cacheName) {
        callCacheApi("/" + cacheName, HttpMethod.GET, null);
    }

    @When("I flush all caches")
    public void flush_all_caches() {
        callCacheApi(StringUtils.EMPTY, HttpMethod.DELETE, null);
    }

    @When("I flush cache {string}")
    public void flush_cache(String cacheName) {
        callCacheApi("/" + cacheName, HttpMethod.DELETE, null);
    }

    @Then("I find caches {string}")
    public void find_caches(String expectedCachesName) throws JsonProcessingException {
        List<String> cachesNames = objectMapper.readValue(holder.getBody(),
                new TypeReference<List<String>>() {
                });
        assertThat(cachesNames).containsExactlyInAnyOrder(expectedCachesName.split(","));
    }

    @Then("I find values:")
    public void find_caches(Map<String, Object> rows) throws JsonProcessingException {
        Map<String, Object> cachesContent = objectMapper.readValue(holder.getBody(),
                new TypeReference<Map<String, Object>>() {
                });
        assertThat(cachesContent).containsExactlyInAnyOrderEntriesOf(rows);
    }

    @Then("I don't find values")
    public void dont_find_values() throws JsonProcessingException {
        Map<String, Object> cachesContent = objectMapper.readValue(holder.getBody(),
                new TypeReference<Map<String, Object>>() {
                });
        assertThat(cachesContent).isEmpty();
    }

    @Then("all caches are empty")
    public void all_caches_are_empty() {
        cacheManager.getCacheNames().forEach(id -> assertThat((Map<?, ?>) cacheManager.getCache(id).getNativeCache()).isEmpty());
    }

    @Then("cache {string} is empty")
    public void cache_is_empty(String cacheName) {
        assertThat((Map<?, ?>) cacheManager.getCache(cacheName).getNativeCache()).isEmpty();
    }

    private <T> void callCacheApi(String path, HttpMethod method, T body) {
        ResponseEntity<?> response = restTemplate.exchange(
                "http://localhost:" + port + contextPath + cacheUrl + path, method,
                new HttpEntity<>(body, holder.getHeaders()), String.class);
        holder.setStatusCode(response.getStatusCode());
        if (response.hasBody()) {
            holder.setBody((String) response.getBody());
        }
    }
}
