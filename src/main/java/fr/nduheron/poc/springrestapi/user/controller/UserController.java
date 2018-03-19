package fr.nduheron.poc.springrestapi.user.controller;

import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;

import javax.annotation.security.RolesAllowed;
import javax.transaction.Transactional;
import javax.validation.Valid;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import fr.nduheron.poc.springrestapi.tools.exception.NotFoundException;
import fr.nduheron.poc.springrestapi.tools.swagger.ApiConflictResponse;
import fr.nduheron.poc.springrestapi.user.ExistException;
import fr.nduheron.poc.springrestapi.user.dto.CreateUserDto;
import fr.nduheron.poc.springrestapi.user.dto.UpdateUserDto;
import fr.nduheron.poc.springrestapi.user.dto.UserDto;
import fr.nduheron.poc.springrestapi.user.mapper.UserMapper;
import fr.nduheron.poc.springrestapi.user.model.User;
import fr.nduheron.poc.springrestapi.user.repository.UserRepository;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value = "${api.basePath}/users")
@Transactional
public class UserController {

	@Autowired
	private UserRepository repo;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private JavaMailSender emailSender;

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private UserMapper mapper;

	@RequestMapping(method = RequestMethod.GET)
	@ApiOperation(value = "Rechercher tous les utilisateurs")
	@RolesAllowed({ "ADMIN", "SYSTEM" })
	public List<UserDto> findAll() {

		List<User> findAll = repo.findAll();
		return mapper.toDto(findAll);
	}

	@RequestMapping(value = "{login}", method = RequestMethod.GET)
	@ApiOperation(value = "Rechercher un utilisateur")
	@RolesAllowed({ "ADMIN", "SYSTEM" })
	public UserDto find(@PathVariable("login") final String login) {
		User user = repo.getOne(login);
		return mapper.toDto(user);
	}

	@RequestMapping(value = "{login}", method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@ApiOperation(value = "Supprimer un utilisateur")
	@RolesAllowed({ "ADMIN" })
	public void supprimer(@PathVariable("login") final String login) {
		Optional<User> user = repo.findById(login);
		if (user.isPresent()) {
			repo.delete(user.get());
		}
	}

	@RequestMapping(method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	@ApiOperation(value = "Créer un utilisateur")
	@RolesAllowed({ "ADMIN" })
	@ApiConflictResponse(message = "L'utilisateur existe déjà")
	public UserDto save(@RequestBody @Valid final CreateUserDto createUser) throws ExistException {

		if (repo.existsById(createUser.getLogin())) {
			throw new ExistException("error.user.alreadyexist", createUser.getLogin());
		}

		User user = mapper.toEntity(createUser);
		String newPassword = randomPassword();
		user.setPassword(passwordEncoder.encode(newPassword));

		user = repo.save(user);

		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom(messageSource.getMessage("user.mdp.reinit.from", null, LocaleContextHolder.getLocale()));
		message.setTo(user.getEmail());
		message.setSubject(messageSource.getMessage("user.mdp.create.objet", null, LocaleContextHolder.getLocale()));
		message.setText(messageSource.getMessage("user.mdp.create.message",
				new Object[] { user.getPrenom(), user.getLogin(), newPassword }, LocaleContextHolder.getLocale()));
		emailSender.send(message);

		return mapper.toDto(user);
	}

	@RequestMapping(value = "{login}", method = RequestMethod.PUT)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@ApiOperation(value = "Modifier un utilisateur")
	@RolesAllowed({ "ADMIN" })
	public void modifier(@PathVariable("login") final String login, @RequestBody @Valid final UpdateUserDto updateUser)
			throws NotFoundException {
		if (!repo.existsById(login)) {
			throw new NotFoundException(String.format("L'utilisateur %s n'existe pas.", login));
		}

		User entity = mapper.toEntity(updateUser);
		entity.setLogin(login);
		repo.save(entity);
	}

	@RequestMapping(value = "/{login}/password", method = RequestMethod.PATCH)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@ApiOperation(value = "Réinitialiser le mot de passe")
	@PreAuthorize("@securityService.isUserAuthorized(#login)")
	public void reinitPassword(@PathVariable("login") final String login) throws NotFoundException {
		User user = repo.getOne(login);
		String newPassword = randomPassword();
		user.setPassword(passwordEncoder.encode(newPassword));

		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom(messageSource.getMessage("user.mdp.reinit.from", null, LocaleContextHolder.getLocale()));
		message.setTo(user.getEmail());
		message.setSubject(messageSource.getMessage("user.mdp.reinit.objet", null, LocaleContextHolder.getLocale()));
		message.setText(messageSource.getMessage("user.mdp.reinit.message",
				new Object[] { user.getPrenom(), newPassword }, LocaleContextHolder.getLocale()));
		emailSender.send(message);

		repo.save(user);
	}

	private String randomPassword() {
		return RandomStringUtils.random(6, 0, 0, true, true, null, new SecureRandom());
	}

}
