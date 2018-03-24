package fr.nduheron.poc.springrestapi.steps;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.springframework.http.HttpMethod;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import fr.nduheron.poc.springrestapi.tools.AbstractCucumberSteps;
import fr.nduheron.poc.springrestapi.user.dto.ChangePasswordDto;
import fr.nduheron.poc.springrestapi.user.dto.UserDto;
import fr.nduheron.poc.springrestapi.user.model.Role;

public class AccountSteps extends AbstractCucumberSteps {

	@When("^I get account$")
	public void i_get_account() {
		callApi("/account", HttpMethod.GET, null);
	}

	@Then("^my name is (.+) (.+)$")
	public void my_name_is(String lastname, String firstname) throws IOException {
		UserDto user = objectMapper.readValue(holder.getBody(), UserDto.class);
		assertEquals(firstname, user.getNom());
		assertEquals(lastname, user.getPrenom());
	}

	@Then("^I am (.+) user$")
	public void i_am_role_user(String role) throws IOException {
		UserDto user = objectMapper.readValue(holder.getBody(), UserDto.class);
		assertEquals(Role.valueOf(role), user.getRole());
	}

	@When("^I change (.+) password with (.+)$")
	public void i_change_password_with(String oldPassword, String newPassword) {
		ChangePasswordDto changePassword = new ChangePasswordDto();
		changePassword.setOldPassword(oldPassword);
		changePassword.setNewPassword(newPassword);
		if (2 == holder.getVersion()) {
			callApi("/account/password", HttpMethod.PATCH, changePassword);
		} else {
			callApi("/account/password", HttpMethod.PUT, changePassword);
		}
	}

}
