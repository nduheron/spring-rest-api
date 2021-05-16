package fr.nduheron.poc.springrestapi.steps;

import com.fasterxml.jackson.core.type.TypeReference;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import fr.nduheron.poc.springrestapi.tools.AbstractCucumberSteps;
import fr.nduheron.poc.springrestapi.user.dto.UpdateUserDto;
import fr.nduheron.poc.springrestapi.user.dto.UserDto;
import fr.nduheron.poc.springrestapi.user.model.Role;
import fr.nduheron.poc.springrestapi.user.model.User;
import fr.nduheron.poc.springrestapi.user.repository.UserRepository;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.transaction.Transactional;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

@Transactional
public class UserSteps extends AbstractCucumberSteps {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @When("^I get all users$")
    public void get_all_users() {
        callApi("/users", HttpMethod.GET, null);
    }

    @When("^I get all users in csv$")
    public void get_all_users_in_csv() {
        holder.getHeaders().add(HttpHeaders.ACCEPT, "text/csv;charset=UTF-8");
        callApi("/users", HttpMethod.GET, null);
    }

    @When("^I search user (\\w+)$")
    public void i_search_user(String login) {
        callApi("/users/" + login, HttpMethod.GET, null);
    }

    @Then("^(\\d+) users found$")
    public void users_found(long nb) throws IOException {
        List<UserDto> users = objectMapper.readValue(holder.getBody(),
                new TypeReference<List<UserDto>>() {
                });
        assertEquals(nb, users.size());
    }

    @Then("^(\\d+) users found in csv$")
    public void users_found_in_csv(long nb) throws IOException {
        try (InputStreamReader reader = new InputStreamReader(
                new ByteArrayInputStream(holder.getBody().getBytes(StandardCharsets.UTF_8)))) {

            ColumnPositionMappingStrategy<UserDto> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(UserDto.class);

            CsvToBean<UserDto> csvToBean = new CsvToBeanBuilder<UserDto>(reader).withMappingStrategy(strategy)
                    .withSkipLines(1).withIgnoreLeadingWhiteSpace(true).build();

            assertEquals(nb, csvToBean.parse().size());
        }
    }

    @Then("^the user has name (.+) (.+)$")
    public void the_user_has_name(String lastname, String firstname) throws IOException {
        UserDto user = objectMapper.readValue(holder.getBody(), UserDto.class);
        assertEquals(firstname, user.getNom());
        assertEquals(lastname, user.getPrenom());
    }

    @When("^I delete user (\\w+)$")
    public void i_delete_user(String login) {
        callApi("/users/" + login, HttpMethod.DELETE, null);
    }

    @Then("^the user (\\w+) is deleted$")
    public void the_user_is_deleted(String login) throws IOException {
        assertFalse(userRepository.existsById(login));
    }

    @When("^I create (\\w+) user$")
    public void i_create_ironman_user(String login) {
        UserDto userDto = new UserDto();
        if (!"empty".equals(login)) {
            userDto.setLogin(login);
            userDto.setEmail("ironman@yopmail.fr");
            userDto.setEnabled(true);
            userDto.setNom("Stark");
            userDto.setPrenom("Tony");
            userDto.setRole(Role.USER);
        }
        callApi("/users", HttpMethod.POST, userDto);
    }

    @Then("^the user (\\w+) is created$")
    public void the_user_is_created(String login) throws IOException {
        Optional<User> userDB = userRepository.findById(login);
        assertTrue(userDB.isPresent());
        UserDto user = objectMapper.readValue(holder.getBody(), UserDto.class);
        assertEquals(user.getEmail(), userDB.get().getEmail());
        assertEquals(user.getNom(), userDB.get().getNom());
        assertEquals(user.getPrenom(), userDB.get().getPrenom());
        assertEquals(user.getRole(), userDB.get().getRole());
        assertNull(user.getDerniereConnexion());
    }


    @When("^I update (.+) with superman data$")
    public void i_update_user_with_superman_data(String login) {
        UpdateUserDto userDto = new UpdateUserDto();
        userDto.setEmail("superman@yopmail.fr");
        userDto.setEnabled(false);
        userDto.setNom("Kent");
        userDto.setPrenom("Clark");
        userDto.setRole(Role.USER);
        callApi("/users/" + login, HttpMethod.PUT, userDto);
    }

    @When("^I update (.+) with empty data$")
    public void i_update_user_with_empty_data(String login) {
        UpdateUserDto userDto = new UpdateUserDto();
        callApi("/users/" + login, HttpMethod.PUT, userDto);
    }

    @Then("^the user (.+) has name (.+) (.+)$")
    public void the_user_has_name(String login, String lastname, String firstname) throws IOException {
        User user = userRepository.getOne(login);
        assertEquals(firstname, user.getNom());
        assertEquals(lastname, user.getPrenom());
    }

    @When("^I reinit password to (\\w+)$")
    public void i_reinit_password_to(String login) {
        callApi("/users/" + login + "/attributes/password", HttpMethod.POST, null);
    }

    @Then("^the password to (\\w+) has changed$")
    public void the_password_to_has_changed(String login) {
        User user = userRepository.getOne(login);
        assertFalse(passwordEncoder.matches("12345", user.getPassword()));
    }

}
