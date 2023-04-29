package fr.nduheron.poc.springrestapi.steps;

import com.fasterxml.jackson.core.type.TypeReference;
import fr.nduheron.poc.springrestapi.tools.AbstractCucumberSteps;
import fr.nduheron.poc.springrestapi.tools.exception.model.Error;
import fr.nduheron.poc.springrestapi.user.dto.login.Token;
import io.cucumber.java.Before;
import io.cucumber.java.ParameterType;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ApiSteps extends AbstractCucumberSteps {

    @Then("I get a {httpStatus} response")
    public void I_get_a_response(final HttpStatus statusCode) {
        assertThat(holder.getStatusCode()).isEqualTo(statusCode);
    }

    @Then("I get {int} parameters in error")
    public void I_get_parameters_in_error(final int nbError) throws IOException {
        List<Error> errors = objectMapper.readValue(holder.getBody(), new TypeReference<>() {
        });
        assertThat(errors).hasSize(nbError);
    }

    @Then("I get a {word} error")
    public void I_get_a_error(final String errorCode) throws IOException {
        List<Error> errors = objectMapper.readValue(holder.getBody(), new TypeReference<>() {
        });
        assertThat(errors.get(0).getCode()).isEqualTo(errorCode);
    }

    @Given("I login with {word}")
    public void I_login_with(String username) throws IOException {
        holder.getHeaders().setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("username", username);
        map.add("password", "12345");
        callApi("/oauth/token", HttpMethod.POST, map);

        if (holder.getStatusCode().is2xxSuccessful()) {
            Token token = objectMapper.readValue(holder.getBody(), Token.class);
            holder.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            holder.getHeaders().add(HttpHeaders.AUTHORIZATION, token.getType() + " " + token.getAccess());
        }
    }

    @Given("version {int}")
    public void version(Integer version) {
        holder.setVersion(version);
    }

    @Before
    public void version() {
        holder.setVersion(1);
    }

    @ParameterType(value = "\\w+", name = "httpStatus")
    public HttpStatus httpStatus(String value) {
        return HttpStatus.valueOf(value);
    }

}
