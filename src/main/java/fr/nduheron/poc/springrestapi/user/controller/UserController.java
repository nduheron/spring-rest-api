package fr.nduheron.poc.springrestapi.user.controller;

import fr.nduheron.poc.springrestapi.api.UtilisateursApi;
import fr.nduheron.poc.springrestapi.dto.CreateUserDto;
import fr.nduheron.poc.springrestapi.dto.UpdateUserDto;
import fr.nduheron.poc.springrestapi.dto.UserDto;
import fr.nduheron.poc.springrestapi.tools.cache.Etag;
import fr.nduheron.poc.springrestapi.tools.cache.EtagEvict;
import fr.nduheron.poc.springrestapi.tools.exception.NotFoundException;
import fr.nduheron.poc.springrestapi.user.ExistException;
import fr.nduheron.poc.springrestapi.user.mapper.UserMapper;
import fr.nduheron.poc.springrestapi.user.model.Role;
import fr.nduheron.poc.springrestapi.user.model.User;
import fr.nduheron.poc.springrestapi.user.repository.UserRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;
import javax.transaction.Transactional;
import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;

@RestController
@Transactional
public class UserController implements UtilisateursApi {

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

    @RolesAllowed({"ADMIN", "SYSTEM"})
    @Etag(cache = "etag-users")
    public ResponseEntity<List<UserDto>> findUsers() {

        List<User> findAll = repo.findAll();
        return ResponseEntity.ok(mapper.toDto(findAll));
    }

    @RolesAllowed({"ADMIN", "SYSTEM"})
    @Etag(cache = "etag-users")
    public ResponseEntity<UserDto> findUser(final String login) {
        User user = repo.getOne(login);
        return ResponseEntity.ok(mapper.toDto(user));
    }

    @RolesAllowed({"ADMIN"})
    @EtagEvict(cache = "etag-users", evictParentResource = true)
    public ResponseEntity<Void> deleteUser(final String login) {
        repo.findById(login).ifPresent(user -> repo.delete(user));
        return ResponseEntity.noContent().build();
    }

    @RolesAllowed({"ADMIN"})
    @EtagEvict(cache = "etag-users")
    public ResponseEntity<UserDto> saveUser(final CreateUserDto createUser) {

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

        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toDto(user));
    }

    @RolesAllowed({"ADMIN"})
    @EtagEvict(cache = "etag-users", evictParentResource = true)
    public ResponseEntity<Void> updateUser(final String login, final UpdateUserDto updateUser)
            throws NotFoundException {

        User user = repo.findById(login).orElseThrow(() -> new NotFoundException(String.format("L'utilisateur %s n'existe pas.", login)));

        user.setEmail(updateUser.getEmail());
        user.setEnabled(updateUser.isEnabled());
        user.setNom(updateUser.getNom());
        user.setPrenom(updateUser.getPrenom());
        user.setRole(Role.valueOf(updateUser.getRole().name()));

        repo.save(user);

        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("@securityService.isUserAuthorized(#login)")
    public ResponseEntity<Void> resetPassword(final String login) {
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

        return ResponseEntity.status(HttpStatus.CREATED).build();

    }

    private String randomPassword() {
        return RandomStringUtils.random(6, 0, 0, true, true, null, new SecureRandom());
    }

}
