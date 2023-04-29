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

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
public class UserSteps extends AbstractCucumberSteps {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserSteps(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @When("I get all users")
    public void get_all_users() {
        callApi("/users", HttpMethod.GET, null);
    }

    @When("I get all users in csv")
    public void get_all_users_in_csv() {
        holder.getHeaders().add(HttpHeaders.ACCEPT, "text/csv;charset=UTF-8");
        callApi("/users", HttpMethod.GET, null);
    }

    @When("I search user {word}")
    public void i_search_user(String login) {
        callApi("/users/" + login, HttpMethod.GET, null);
    }

    @Then("{int} users found")
    public void users_found(int nb) throws IOException {
        List<UserDto> users = objectMapper.readValue(holder.getBody(), new TypeReference<>() {
        });
        assertThat(users).hasSize(nb);
    }

    @Then("{int} users found in csv")
    public void users_found_in_csv(int nb) throws IOException {
        try (InputStreamReader reader = new InputStreamReader(
                new ByteArrayInputStream(holder.getBody().getBytes(StandardCharsets.UTF_8)))) {

            ColumnPositionMappingStrategy<UserDto> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(UserDto.class);

            CsvToBean<UserDto> csvToBean = new CsvToBeanBuilder<UserDto>(reader).withMappingStrategy(strategy)
                    .withSkipLines(1).withIgnoreLeadingWhiteSpace(true).build();

            assertThat(csvToBean.parse()).hasSize(nb);
        }
    }

    @Then("the user has name {word} {word}")
    public void the_user_has_name(String lastname, String firstname) throws IOException {
        UserDto user = objectMapper.readValue(holder.getBody(), UserDto.class);
        assertThat(user.getNom()).isEqualTo(firstname);
        assertThat(user.getPrenom()).isEqualTo(lastname);
    }

    @When("I delete user {word}")
    public void i_delete_user(String login) {
        callApi("/users/" + login, HttpMethod.DELETE, null);
    }

    @Then("the user {word} is deleted")
    public void the_user_is_deleted(String login) {
        assertThat(userRepository.existsById(login)).isFalse();
    }

    @When("I create {word} user")
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

    @Then("the user {word} is created")
    public void the_user_is_created(String login) throws IOException {
        Optional<User> userDB = userRepository.findById(login);
        UserDto user = objectMapper.readValue(holder.getBody(), UserDto.class);
        assertThat(userDB)
                .isPresent()
                .get()
                .hasFieldOrPropertyWithValue("email", user.getEmail())
                .hasFieldOrPropertyWithValue("nom", user.getNom())
                .hasFieldOrPropertyWithValue("prenom", user.getPrenom())
                .hasFieldOrPropertyWithValue("role", user.getRole())
                .hasFieldOrPropertyWithValue("derniereConnexion", null);
    }

    @When("I update {word} with superman data")
    public void i_update_user_with_superman_data(String login) {
        UpdateUserDto userDto = new UpdateUserDto();
        userDto.setEmail("superman@yopmail.fr");
        userDto.setEnabled(false);
        userDto.setNom("Kent");
        userDto.setPrenom("Clark");
        userDto.setRole(Role.USER);
        callApi("/users/" + login, HttpMethod.PUT, userDto);
    }

    @When("I update {word} with empty data")
    public void i_update_user_with_empty_data(String login) {
        UpdateUserDto userDto = new UpdateUserDto();
        callApi("/users/" + login, HttpMethod.PUT, userDto);
    }

    @Then("the user {word} has name {word} {word}")
    public void the_user_has_name(String login, String lastname, String firstname) {
        User user = userRepository.getReferenceById(login);
        assertThat(user.getNom()).isEqualTo(firstname);
        assertThat(user.getPrenom()).isEqualTo(lastname);

    }

    @When("I reinit password to {word}")
    public void i_reinit_password_to(String login) {
        callApi("/users/" + login + "/attributes/password", HttpMethod.POST, null);
    }

    @Then("the password to {word} has changed")
    public void the_password_to_has_changed(String login) {
        User user = userRepository.getReferenceById(login);
        assertThat(passwordEncoder.matches("12345", user.getPassword())).isFalse();
    }

}
