package fr.nduheron.poc.springrestapi.tools;

import fr.nduheron.poc.springrestapi.config.DBUnitConfiguration;
import fr.nduheron.poc.springrestapi.config.MockConfiguration;
import io.cucumber.java.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration
@ActiveProfiles("test")
@Import({DBUnitConfiguration.class, MockConfiguration.class})
public class CucumberSpringContextConfiguration {


    private static final Logger LOG = LoggerFactory.getLogger(CucumberSpringContextConfiguration.class);

    /**
     * Need this method so the cucumber will recognize this class as glue and load spring context configuration
     */
    @Before
    public void setUp() {
        LOG.info("-------------- Spring Context Initialized For Executing Cucumber Tests --------------");
    }
}
