package fr.nduheron.poc.springrestapi.doc;

import com.google.common.io.Resources;
import fr.nduheron.poc.springrestapi.config.DBUnitConfiguration;
import fr.nduheron.poc.springrestapi.config.MockConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.skyscreamer.jsonassert.Customization;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.comparator.CustomComparator;
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

import java.nio.charset.StandardCharsets;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration
@ActiveProfiles("test")
@Import({DBUnitConfiguration.class, MockConfiguration.class})
public class OpenApiIntegrationTest {

    @LocalServerPort
    private int port;
    @Value("${server.servlet.context-path:}")
    private String contextPath;
    private TestRestTemplate restTemplate = new TestRestTemplate();


    @Test
    public void swaggerUserApi() throws Exception {
        ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:" + port + contextPath + Constants.DEFAULT_API_DOCS_URL, String.class);
        JSONAssert.assertEquals(
                response.getBody(),
                Resources.toString(getClass().getResource("/swagger.json"), StandardCharsets.UTF_8),
                new CustomComparator(
                        JSONCompareMode.STRICT,
                        new Customization("tokenUrl", (o1, o2) -> true),
                        new Customization("servers", (o1, o2) -> true),
                        new Customization("components.securitySchemes.oauthPasswordFlow.flows.password.tokenUrl", (o1, o2) -> true)
                )
        );
    }
}