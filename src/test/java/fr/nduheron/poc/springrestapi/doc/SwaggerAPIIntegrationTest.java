package fr.nduheron.poc.springrestapi.doc;

import fr.nduheron.poc.springrestapi.config.DBUnitConfiguration;
import fr.nduheron.poc.springrestapi.config.MockConfiguration;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springdoc.core.Constants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration
@ActiveProfiles("test")
@Import({DBUnitConfiguration.class, MockConfiguration.class})
public class SwaggerAPIIntegrationTest {

    @LocalServerPort
    private int port;
    @Value("${server.servlet.context-path:}")
    private String contextPath;
    private TestRestTemplate restTemplate = new TestRestTemplate();


    @Test
    public void swaggerUserApi() throws Exception {
        ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:" + port + contextPath + Constants.DEFAULT_API_DOCS_URL, String.class);
        String contentAsString = response.getBody();
        try (Writer writer = new FileWriter(new File("target/swagger.json"))) {
            IOUtils.write(contentAsString, writer);
        }
    }
}