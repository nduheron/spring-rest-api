package fr.nduheron.poc.springrestapi.tools;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.nduheron.poc.springrestapi.config.DBUnitConfiguration;
import fr.nduheron.poc.springrestapi.config.MockConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import javax.transaction.Transactional;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration
@ActiveProfiles("test")
@Import({DBUnitConfiguration.class, MockConfiguration.class})
@Transactional
public abstract class AbstractCucumberSteps {


    @Autowired
    protected Holder holder;
    @Autowired
    protected JavaMailSender javaMailSender;
    @Autowired
    protected ObjectMapper objectMapper;
    @LocalServerPort
    private int port;
    @Value("${server.servlet.context-path:}")
    private String contextPath;
    private TestRestTemplate restTemplate = new TestRestTemplate();

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
    }

}
