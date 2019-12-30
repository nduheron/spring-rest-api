package fr.nduheron.poc.springrestapi.steps;

import static org.junit.Assert.assertEquals;

import com.fasterxml.jackson.core.type.TypeReference;
import fr.nduheron.poc.springrestapi.dto.TokenDto;
import fr.nduheron.poc.springrestapi.tools.AbstractCucumberSteps;
import fr.nduheron.poc.springrestapi.tools.exception.model.Error;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import java.io.IOException;
import java.util.List;
import org.dbunit.IDatabaseTester;
import org.dbunit.dataset.CompositeDataSet;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class CommonSteps extends AbstractCucumberSteps {

  @Autowired private IDatabaseTester dbTester;

  @Then("^I get a (.+) response$")
  public void I_get_a_response(final String statusCode) {
    assertEquals(HttpStatus.valueOf(statusCode), holder.getStatusCode());
  }

  @Then("^I get (\\d+) parameters in error$")
  public void I_get_parameters_in_error(final int nbError) throws IOException {
    List<Error> errors =
        objectMapper.readValue(holder.getBody(), new TypeReference<List<Error>>() {});
    assertEquals(nbError, errors.size());
  }

  @Then("^I get a (\\w+) error$")
  public void I_get_a_error(final String errorCode) throws IOException {
    List<Error> errors =
        objectMapper.readValue(holder.getBody(), new TypeReference<List<Error>>() {});
    assertEquals(errorCode, errors.get(0).getCode());
  }

  @Given("^I login with (\\w+)$")
  public void I_login_with(String username) throws IOException {
    holder.getHeaders().setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
    map.add("username", username);
    map.add("password", "12345");
    map.add("grant_type", "password");
    callApi("/oauth/token", HttpMethod.POST, map);

    if (holder.getStatusCode().is2xxSuccessful()) {
      TokenDto token = objectMapper.readValue(holder.getBody(), TokenDto.class);
      holder.getHeaders().setContentType(MediaType.APPLICATION_JSON);
      holder
          .getHeaders()
          .add(HttpHeaders.AUTHORIZATION, token.getTokenType() + " " + token.getAccessToken());
    }
  }

  @Given("^(.+) datasets$")
  public void loadDatasets(String datasetsfilename) throws Exception {
    String[] dataSets = datasetsfilename.split(",");
    IDataSet[] idataSets = new IDataSet[dataSets.length];
    for (int i = 0; i < dataSets.length; i++) {
      idataSets[i] =
          new FlatXmlDataSetBuilder()
              .build(getClass().getResourceAsStream("/datasets/" + dataSets[i] + ".xml"));
    }

    dbTester.setDataSet(new CompositeDataSet(idataSets));
    dbTester.onSetup();
  }

  @Given("^version (\\d)")
  public void version(Integer version) {
    holder.setVersion(version);
  }

  @Before
  public void version() {
    holder.setVersion(1);
  }

  @After("@dbunit")
  public void closeDbTester() throws Exception {
    dbTester.onTearDown();
  }

  @Before
  public void initAllScenarii() throws Exception {
    Mockito.reset(javaMailSender);
  }
}
