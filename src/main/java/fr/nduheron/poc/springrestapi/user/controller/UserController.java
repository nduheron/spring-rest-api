package fr.nduheron.poc.springrestapi.user.controller;

import fr.nduheron.poc.springrestapi.tools.cache.Etag;
import fr.nduheron.poc.springrestapi.tools.cache.EtagEvict;
import fr.nduheron.poc.springrestapi.tools.exception.NotFoundException;
import fr.nduheron.poc.springrestapi.tools.openapi.annotations.ApiDefaultResponse400;
import fr.nduheron.poc.springrestapi.tools.openapi.annotations.AuthPasswordOperation;
import fr.nduheron.poc.springrestapi.user.ExistException;
import fr.nduheron.poc.springrestapi.user.dto.CreateUserDto;
import fr.nduheron.poc.springrestapi.user.dto.UpdateUserDto;
import fr.nduheron.poc.springrestapi.user.dto.UserDto;
import fr.nduheron.poc.springrestapi.user.mapper.UserMapper;
import fr.nduheron.poc.springrestapi.user.model.User;
import fr.nduheron.poc.springrestapi.user.repository.UserRepository;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
@Tag(name = "Utilisateurs")
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

    @GetMapping(produces = {MediaType.APPLICATION_JSON_UTF8_VALUE, "text/csv;charset=UTF-8"})
    @AuthPasswordOperation(summary = "Rechercher tous les utilisateurs")
    @ApiResponse(responseCode = "200", content = {
            @Content(mediaType = MediaType.APPLICATION_JSON_UTF8_VALUE, array = @ArraySchema(schema = @Schema(implementation = UserDto.class))),
            @Content(mediaType = "text/csv;charset=UTF-8", array = @ArraySchema(schema = @Schema(implementation = UserDto.class)), examples = @ExampleObject(value = "LOGIN,NOM,PRENOM,EMAIL,ROLE\nbatman,Wayne,Bruce,batman@yopmail.fr,SYSTEM")),
    })
    @RolesAllowed({"ADMIN", "SYSTEM"})
    @Etag(cache = "etag-users")
    public List<UserDto> findUsers() {

        List<User> findAll = repo.findAll();
        return mapper.toDto(findAll);
    }

    @GetMapping("{login}")
    @AuthPasswordOperation(summary = "Rechercher un utilisateur")
    @RolesAllowed({"ADMIN", "SYSTEM"})
    @Etag(cache = "etag-users")
    public UserDto findUser(@PathVariable("login") @Parameter(example = "batman", required = true) final String login) {
        User user = repo.getOne(login);
        return mapper.toDto(user);
    }

    @DeleteMapping("{login}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @AuthPasswordOperation(summary = "Supprimer un utilisateur")
    @RolesAllowed({"ADMIN"})
    @EtagEvict(cache = "etag-users", evictParentResource = true)
    public void deleteUser(@PathVariable("login") @Parameter(example = "batman", required = true) final String login) {
        repo.findById(login).ifPresent(user -> repo.delete(user));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @AuthPasswordOperation(summary = "Créer un utilisateur")
    @RolesAllowed({"ADMIN"})
    @ApiResponse(responseCode = "400", content = @Content(array = @ArraySchema(schema = @Schema(ref = "Error")), examples = {
            @ExampleObject(ref = "InvalidFormat"),
            @ExampleObject(name = "AlreadyExist", value = "[{\"code\": \"ALREADY_EXIST\", \"message\": \"User batman already exist\"}]")
    }))
    @EtagEvict(cache = "etag-users")
    public UserDto saveUser(@RequestBody @Valid final CreateUserDto createUser) throws ExistException {

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
    @AuthPasswordOperation(summary = "Modifier un utilisateur")
    @RolesAllowed({"ADMIN"})
    @EtagEvict(cache = "etag-users", evictParentResource = true)
    @ApiDefaultResponse400
    public void updateUser(@PathVariable("login") @Parameter(example = "batman", required = true) final String login, @RequestBody @Valid final UpdateUserDto updateUser)
            throws NotFoundException {

        User user = repo.findById(login).orElseThrow(() -> new NotFoundException(String.format("L'utilisateur %s n'existe pas.", login)));

        user.setEmail(updateUser.getEmail());
        user.setEnabled(updateUser.isEnabled());
        user.setNom(updateUser.getNom());
        user.setPrenom(updateUser.getPrenom());
        user.setRole(updateUser.getRole());

        repo.save(user);
    }

    @PostMapping("/{login}/attributes/password")
    @ResponseStatus(HttpStatus.CREATED)
    @AuthPasswordOperation(summary = "Réinitialiser le mot de passe")
    @PreAuthorize("@securityService.isUserAuthorized(#login)")
    public void resetPassword(@PathVariable("login") @Parameter(example = "batman", required = true) final String login) {
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
