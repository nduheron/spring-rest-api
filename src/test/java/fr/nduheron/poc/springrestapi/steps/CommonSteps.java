package fr.nduheron.poc.springrestapi.steps;

import static org.junit.Assert.assertEquals;

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

import com.fasterxml.jackson.core.type.TypeReference;

import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import fr.nduheron.poc.springrestapi.tools.AbstractCucumberSteps;
import fr.nduheron.poc.springrestapi.tools.exception.model.ErrorParameter;
import fr.nduheron.poc.springrestapi.tools.exception.model.FunctionalError;
import fr.nduheron.poc.springrestapi.user.dto.LoginDto;

public class CommonSteps extends AbstractCucumberSteps {

	@Autowired
	private IDatabaseTester dbTester;

	@Then("^I get a (.+) response$")
	public void I_get_a_response(final String statusCode) throws Throwable {
		assertEquals(HttpStatus.valueOf(statusCode), holder.getStatusCode());
	}

	@Then("^I get (\\d+) parameters in error$")
	public void I_get_parameters_in_error(final int nbError) throws Throwable {
		List<ErrorParameter> errors = objectMapper.readValue(holder.getBody(),
				new TypeReference<List<ErrorParameter>>() {
				});
		assertEquals(nbError, errors.size());
	}

	@Then("^I get a (\\w+) error$")
	public void I_get_a_error(final String errorCode) throws Throwable {
		FunctionalError error = objectMapper.readValue(holder.getBody(), FunctionalError.class);
		assertEquals(errorCode, error.getCode());
	}

	@Given("^I login with (\\w+)$")
	public void I_login_with(String username) {
		LoginDto login = new LoginDto();
		login.setUsername(username);
		login.setPassword("12345");

		callApi("/auth", HttpMethod.POST, login);
		if (holder.getStatusCode().is2xxSuccessful()) {
			holder.getHeaders().add(HttpHeaders.AUTHORIZATION, "Bearer " + holder.getBody());
		}
	}

	@Given("^(.+) datasets$")
	public void loadDatasets(String datasetsfilename) throws Exception {
		String[] dataSets = datasetsfilename.split(",");
		IDataSet[] idataSets = new IDataSet[dataSets.length];
		for (int i = 0; i < dataSets.length; i++) {
			idataSets[i] = new FlatXmlDataSetBuilder()
					.build(getClass().getResourceAsStream("/datasets/" + dataSets[i] + ".xml"));
		}

		dbTester.setDataSet(new CompositeDataSet(idataSets));
		dbTester.onSetup();
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
