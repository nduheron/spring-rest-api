package fr.nduheron.poc.springrestapi;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(features = {"src/test/resources/features"}, plugin = {"json:target/cucumber.json"})
public class CucumberIntegrationIT {

}
