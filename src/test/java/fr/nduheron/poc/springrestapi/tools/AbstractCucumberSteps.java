package fr.nduheron.poc.springrestapi.tools;

import javax.transaction.Transactional;

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

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.nduheron.poc.springrestapi.config.DBUnitConfiguration;
import fr.nduheron.poc.springrestapi.config.MockConfiguration;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration
@ActiveProfiles("test")
@Import({ DBUnitConfiguration.class, MockConfiguration.class })
@Transactional
public abstract class AbstractCucumberSteps {

	@Value("${api.basePath}")
	private String basePath;

	@LocalServerPort
	private int port;

	@Autowired
	protected Holder holder;

	private TestRestTemplate restTemplate = new TestRestTemplate();

	@Autowired
	protected JavaMailSender javaMailSender;

	@Autowired
	protected ObjectMapper objectMapper;

	protected <T> void callApi(String path, HttpMethod method, T body) {
		ResponseEntity<?> response = restTemplate.exchange("http://localhost:" + port + basePath + path, method,
				new HttpEntity<>(body, holder.getHeaders()), String.class);
		holder.setStatusCode(response.getStatusCode());
		if (response.hasBody()) {
			holder.setBody((String) response.getBody());
		}
	}

}
