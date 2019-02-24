package fr.nduheron.poc.springrestapi.user.controller;

import fr.nduheron.poc.springrestapi.tools.exception.NotFoundException;
import fr.nduheron.poc.springrestapi.tools.exception.model.Error;
import fr.nduheron.poc.springrestapi.tools.swagger.ApiBadRequestResponse;
import fr.nduheron.poc.springrestapi.tools.swagger.ErrorDocumentation;
import fr.nduheron.poc.springrestapi.user.ExistException;
import fr.nduheron.poc.springrestapi.user.dto.CreateUserDto;
import fr.nduheron.poc.springrestapi.user.dto.UpdateUserDto;
import fr.nduheron.poc.springrestapi.user.dto.UserDto;
import fr.nduheron.poc.springrestapi.user.mapper.UserMapper;
import fr.nduheron.poc.springrestapi.user.model.User;
import fr.nduheron.poc.springrestapi.user.repository.UserRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/v1/users")
@Transactional
@Api(tags = "Utilisateurs")
@Validated
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

    @GetMapping
    @ApiOperation(value = "Rechercher tous les utilisateurs")
    @RolesAllowed({"ADMIN", "SYSTEM"})
    public List<UserDto> findAll() {

        List<User> findAll = repo.findAll();
        return mapper.toDto(findAll);
    }

    @GetMapping("{login}")
    @ApiOperation("Rechercher un utilisateur")
    @RolesAllowed({"ADMIN", "SYSTEM"})
    @Cacheable("users")
    public UserDto find(@PathVariable("login") @ApiParam(example = "batman", required = true) final String login) {
        User user = repo.getOne(login);
        return mapper.toDto(user);
    }

    @DeleteMapping("{login}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiOperation("Supprimer un utilisateur")
    @RolesAllowed({"ADMIN"})
    @CacheEvict("users")
    public void supprimer(@PathVariable("login") @ApiParam(example = "batman", required = true) final String login) {
        Optional<User> user = repo.findById(login);
        if (user.isPresent()) {
            repo.delete(user.get());
        }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation("Créer un utilisateur")
    @RolesAllowed({"ADMIN"})
    @ApiBadRequestResponse({@ErrorDocumentation(code = Error.REQUIRED), @ErrorDocumentation(code = Error.INVALID_PARAMETER), @ErrorDocumentation(code = ExistException.ALREADY_EXIST, additionalsInformationsType = UserDto.class)})
    public UserDto save(@RequestBody @Valid final CreateUserDto createUser) throws ExistException {

        Optional<User> optionalUser = repo.findById(createUser.getLogin());
        if (optionalUser.isPresent()) {
            ExistException existException = new ExistException("error.user.alreadyexist", createUser.getLogin());
            existException.setAdditionalsInformations(mapper.toDto(optionalUser.get()));
            throw existException;
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
                new Object[]{user.getPrenom(), user.getLogin(), newPassword}, LocaleContextHolder.getLocale()));
        emailSender.send(message);

        return mapper.toDto(user);
    }

    @PutMapping("{login}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiOperation("Modifier un utilisateur")
    @RolesAllowed({"ADMIN"})
    @ApiBadRequestResponse
    public void modifier(@PathVariable("login") @ApiParam(example = "batman", required = true) final String login, @RequestBody @Valid final UpdateUserDto updateUser)
            throws NotFoundException {
        if (!repo.existsById(login)) {
            throw new NotFoundException(String.format("L'utilisateur %s n'existe pas.", login));
        }

        User entity = mapper.toEntity(updateUser);
        entity.setLogin(login);
        repo.save(entity);
    }

    @PatchMapping("/{login}/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiOperation("Réinitialiser le mot de passe")
    @PreAuthorize("@securityService.isUserAuthorized(#login)")
    public void reinitPassword(@PathVariable("login") @ApiParam(example = "batman", required = true) final String login) throws NotFoundException {
        User user = repo.getOne(login);
        String newPassword = randomPassword();
        user.setPassword(passwordEncoder.encode(newPassword));

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(messageSource.getMessage("user.mdp.reinit.from", null, LocaleContextHolder.getLocale()));
        message.setTo(user.getEmail());
        message.setSubject(messageSource.getMessage("user.mdp.reinit.objet", null, LocaleContextHolder.getLocale()));
        message.setText(messageSource.getMessage("user.mdp.reinit.message",
                new Object[]{user.getPrenom(), newPassword}, LocaleContextHolder.getLocale()));
        emailSender.send(message);

        repo.save(user);
    }

    private String randomPassword() {
        return RandomStringUtils.random(6, 0, 0, true, true, null, new SecureRandom());
    }

}
