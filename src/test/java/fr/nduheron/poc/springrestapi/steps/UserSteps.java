package fr.nduheron.poc.springrestapi.steps;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.fasterxml.jackson.core.type.TypeReference;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import fr.nduheron.poc.springrestapi.tools.AbstractCucumberSteps;
import fr.nduheron.poc.springrestapi.user.dto.UpdateUserDto;
import fr.nduheron.poc.springrestapi.user.dto.UserDto;
import fr.nduheron.poc.springrestapi.user.model.Role;
import fr.nduheron.poc.springrestapi.user.model.User;
import fr.nduheron.poc.springrestapi.user.repository.UserRepository;

public class UserSteps extends AbstractCucumberSteps {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private MessageSource messageSource;
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
		List<Resource<UserDto>> users = objectMapper.readValue(holder.getBody(),
				new TypeReference<List<Resource<UserDto>>>() {
				});
		assertEquals(nb, users.size());
	}

	@Then("^all users are updatable$")
	public void all_users_are_updatable() throws IOException {
		List<Resource<UserDto>> users = objectMapper.readValue(holder.getBody(),
				new TypeReference<List<Resource<UserDto>>>() {
				});
		users.forEach(u -> assertNotNull(u.getLink("edit")));
	}

	@Then("^only user (\\w+) is updatable$")
	public void all_users_are_updatable(String login) throws IOException {
		List<Resource<UserDto>> users = objectMapper.readValue(holder.getBody(),
				new TypeReference<List<Resource<UserDto>>>() {
				});
		users.forEach(u -> {
			if (u.getContent().getLogin().equals(login)) {
				assertNotNull(u.getLink("edit"));
			} else {
				assertTrue(u.getLinks().isEmpty());
			}
		});
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

	@Then("^email password is sent$")
	public void email_password_is_send() throws Throwable {
		ArgumentCaptor<SimpleMailMessage> capture = ArgumentCaptor.forClass(SimpleMailMessage.class);
		verify(javaMailSender).send(capture.capture());
		assertEquals(messageSource.getMessage("user.mdp.create.objet", null, Locale.FRANCE),
				capture.getValue().getSubject());
		assertNotNull(capture.getValue().getText());
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
		callApi("/users/" + login + "/password", HttpMethod.PATCH, null);
	}

	@Then("^the password to (\\w+) has changed$")
	public void the_password_to_has_changed(String login) {
		User user = userRepository.getOne(login);
		assertFalse(passwordEncoder.matches("12345", user.getPassword()));
	}

	@Then("^email reinit password is sent$")
	public void email_reinit_password_is_send() throws Throwable {
		ArgumentCaptor<SimpleMailMessage> capture = ArgumentCaptor.forClass(SimpleMailMessage.class);
		verify(javaMailSender).send(capture.capture());
		assertEquals(messageSource.getMessage("user.mdp.reinit.objet", null, Locale.ENGLISH),
				capture.getValue().getSubject());
		assertNotNull(capture.getValue().getText());
	}

}
