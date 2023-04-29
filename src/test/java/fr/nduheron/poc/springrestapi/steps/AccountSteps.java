package fr.nduheron.poc.springrestapi.steps;

import fr.nduheron.poc.springrestapi.tools.AbstractCucumberSteps;
import fr.nduheron.poc.springrestapi.user.dto.ChangePasswordDto;
import fr.nduheron.poc.springrestapi.user.dto.UserDto;
import fr.nduheron.poc.springrestapi.user.model.Role;
import io.cucumber.java.ParameterType;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.http.HttpMethod;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class AccountSteps extends AbstractCucumberSteps {

    @When("I get account")
    public void i_get_account() {
        callApi("/accounts/me", HttpMethod.GET, null);
    }

    @Then("my name is {word} {word}")
    public void my_name_is(String lastname, String firstname) throws IOException {
        UserDto user = objectMapper.readValue(holder.getBody(), UserDto.class);
        assertThat(user.getNom()).isEqualTo(firstname);
        assertThat(user.getPrenom()).isEqualTo(lastname);
    }

    @Then("I am {role} user")
    public void i_am_role_user(Role role) throws IOException {
        UserDto user = objectMapper.readValue(holder.getBody(), UserDto.class);
        assertThat(user.getRole()).isEqualTo(role);
    }

    @When("I change {word} password with {word}")
    public void i_change_password_with(String oldPassword, String newPassword) {
        ChangePasswordDto changePassword = new ChangePasswordDto();
        changePassword.setOldPassword(oldPassword);
        changePassword.setNewPassword(newPassword);
        callApi("/accounts/me/attributes/password", HttpMethod.PUT, changePassword);
    }

    @ParameterType(value = "\\w+", name = "role")
    public Role role(String value) {
        return Role.valueOf(value);
    }

}
