package fr.nduheron.poc.springrestapi.steps;

import cucumber.api.java.en.When;
import fr.nduheron.poc.springrestapi.tools.AbstractCucumberSteps;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class AuthentificationSteps extends AbstractCucumberSteps {

    @When("^I login with username (.+) and password (.+)$")
    public void I_login_with_username_and_password(String username, String password) {

        holder.getHeaders().setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("username", username);
        map.add("password", password);

        callApi("/oauth/token", HttpMethod.POST, map);
    }
}
