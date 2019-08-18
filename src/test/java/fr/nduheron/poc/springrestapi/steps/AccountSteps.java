package fr.nduheron.poc.springrestapi.steps;

import fr.nduheron.poc.springrestapi.tools.AbstractCucumberSteps;
import fr.nduheron.poc.springrestapi.user.dto.ChangePasswordDto;
import fr.nduheron.poc.springrestapi.user.dto.UserDto;
import fr.nduheron.poc.springrestapi.user.model.Role;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Assert;
import org.springframework.http.HttpMethod;

import java.io.IOException;

public class AccountSteps extends AbstractCucumberSteps {

    @When("^I get account$")
    public void i_get_account() {
        callApi("/accounts/me", HttpMethod.GET, null);
    }

    @Then("^my name is (.+) (.+)$")
    public void my_name_is(String lastname, String firstname) throws IOException {
        UserDto user = objectMapper.readValue(holder.getBody(), UserDto.class);
        Assert.assertEquals(firstname, user.getNom());
        Assert.assertEquals(lastname, user.getPrenom());
    }

    @Then("^I am (.+) user$")
    public void i_am_role_user(String role) throws IOException {
        UserDto user = objectMapper.readValue(holder.getBody(), UserDto.class);
        Assert.assertEquals(Role.valueOf(role), user.getRole());
    }

    @When("^I change (.+) password with (.+)$")
    public void i_change_password_with(String oldPassword, String newPassword) {
        ChangePasswordDto changePassword = new ChangePasswordDto();
        changePassword.setOldPassword(oldPassword);
        changePassword.setNewPassword(newPassword);
        callApi("/accounts/me/attributes/password", HttpMethod.PUT, changePassword);
    }

}
