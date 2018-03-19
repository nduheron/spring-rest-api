package fr.nduheron.poc.springrestapi.user.controller;

import javax.annotation.security.PermitAll;
import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import fr.nduheron.poc.springrestapi.user.dto.ChangePasswordDto;
import fr.nduheron.poc.springrestapi.user.dto.UserDto;
import fr.nduheron.poc.springrestapi.user.model.User;
import fr.nduheron.poc.springrestapi.user.repository.UserRepository;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value = "${api.basePath}/account")
@Transactional
public class AccountController {

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private UserRepository repo;

	@RequestMapping(value = "password", method = RequestMethod.PUT)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@ApiOperation(value = "Changer de mot de passe")
	@PermitAll
	public void changePassword(Authentication authentication,
			@RequestBody @Valid final ChangePasswordDto changePassword) {
		UserDto userConnecte = (UserDto) authentication.getPrincipal();
		User user = repo.getOne(userConnecte.getLogin());
		if (passwordEncoder.matches(changePassword.getOldPassword(), user.getPassword())) {
			user.setPassword(passwordEncoder.encode(changePassword.getNewPassword()));
			repo.save(user);
		} else {
			throw new AccessDeniedException("Le mot de passe est invalide.");
		}
	}

	@RequestMapping(method = RequestMethod.GET)
	@ApiOperation(value = "Récupérer le profil de l'utilisateur conneté")
	@PermitAll
	public UserDto find(Authentication authentication) {
		UserDto user = (UserDto) authentication.getPrincipal();
		return user;
	}

}
