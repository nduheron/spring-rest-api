package fr.nduheron.poc.springrestapi.user.controller;

import fr.nduheron.poc.springrestapi.tools.rest.security.jwt.JwtGenerator;
import fr.nduheron.poc.springrestapi.user.dto.login.Token;
import fr.nduheron.poc.springrestapi.user.dto.login.TokenRequest;
import fr.nduheron.poc.springrestapi.user.mapper.UserMapper;
import fr.nduheron.poc.springrestapi.user.model.User;
import fr.nduheron.poc.springrestapi.user.repository.UserRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.Optional;

import static java.lang.String.format;

@RestController
@RequestMapping("/v1/oauth")
@Transactional
@Validated
@Tag(name = "Authentification")
public class AuthentificationController {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository repo;
    private final JwtGenerator jwtGenerator;
    private final UserMapper userMapper;

    public AuthentificationController(PasswordEncoder passwordEncoder, UserRepository repo, JwtGenerator jwtGenerator, UserMapper userMapper) {
        this.passwordEncoder = passwordEncoder;
        this.repo = repo;
        this.jwtGenerator = jwtGenerator;
        this.userMapper = userMapper;
    }

    @PostMapping(value = "/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public Token login(@RequestBody @Valid TokenRequest tokenRequest) {

        Optional<User> user = repo.findById(tokenRequest.getUsername());
        if (!user.isPresent()) {
            throw new UsernameNotFoundException(format("L'utilisateur %s n'existe pas.", tokenRequest.getUsername()));
        }

        if (!user.get().isEnabled()) {
            throw new DisabledException(format("L'utilisateur %s n'est pas actif.", tokenRequest.getUsername()));
        }

        if (passwordEncoder.matches(tokenRequest.getPassword(), user.get().getPassword())) {
            user.get().setDerniereConnexion(LocalDateTime.now());
            String jwt = jwtGenerator.generateToken(userMapper.toDto(user.get()), tokenRequest.getUsername());
            return new Token(jwt, jwtGenerator.getDuration().getSeconds());
        }
        throw new BadCredentialsException("Login/mot de passe incorrect.");
    }
}
