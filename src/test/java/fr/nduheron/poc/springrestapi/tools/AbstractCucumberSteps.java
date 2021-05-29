package fr.nduheron.poc.springrestapi.tools;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;


public abstract class AbstractCucumberSteps {
    @Autowired
    protected Holder holder;
    @Autowired
    protected ObjectMapper objectMapper;
    @LocalServerPort
    protected int port;
    @Value("${server.servlet.context-path:}")
    protected String contextPath;
    protected TestRestTemplate restTemplate = new TestRestTemplate();

    protected <T> void callApi(String path, HttpMethod method, T body) {
        callApi(holder.getVersion(), path, method, body);
    }

    protected <T> void callApi(int version, String path, HttpMethod method, T body) {
        ResponseEntity<?> response = restTemplate.exchange(
                "http://localhost:" + port + contextPath + "/v" + version + path, method,
                new HttpEntity<>(body, holder.getHeaders()), String.class);
        holder.setStatusCode(response.getStatusCode());
        if (response.hasBody()) {
            holder.setBody((String) response.getBody());
        }
        if (response.getHeaders().containsKey(HttpHeaders.ETAG)) {
            holder.setEtag(response.getHeaders().getETag());
        }
    }

}
