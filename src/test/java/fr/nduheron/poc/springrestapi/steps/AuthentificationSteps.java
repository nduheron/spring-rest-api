package fr.nduheron.poc.springrestapi.steps;

import org.springframework.http.HttpMethod;

import cucumber.api.java.en.When;
import fr.nduheron.poc.springrestapi.tools.AbstractCucumberSteps;
import fr.nduheron.poc.springrestapi.user.dto.LoginDto;

public class AuthentificationSteps extends AbstractCucumberSteps {

	@When("^I login with username (.+) and password (.+)$")
	public void I_login_with_username_and_password(String username, String password) {
		LoginDto login = new LoginDto();
		login.setUsername(username);
		login.setPassword(password);

		callApi("/auth", HttpMethod.POST, login);
	}
}
