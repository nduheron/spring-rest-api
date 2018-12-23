package fr.nduheron.poc.springrestapi.user.controller;

import fr.nduheron.poc.springrestapi.tools.security.service.TokenService;
import fr.nduheron.poc.springrestapi.user.dto.LoginDto;
import fr.nduheron.poc.springrestapi.user.mapper.UserMapper;
import fr.nduheron.poc.springrestapi.user.model.User;
import fr.nduheron.poc.springrestapi.user.repository.UserRepository;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/v1/auth")
@Transactional
@Api(tags = "Authentification")
public class AuthentificationController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository repo;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UserMapper userMapper;

    @PostMapping
    public String login(@RequestBody @Valid final LoginDto login) {
        Optional<User> user = repo.findById(login.getUsername());
        if (!user.isPresent()) {
            throw new UsernameNotFoundException(String.format("L'utilisateur %s n'existe pas.", login.getUsername()));
        }

        if (!user.get().isEnabled()) {
            throw new DisabledException(String.format("L'utilisateur %s n'est pas actif.", login.getUsername()));
        }

        if (passwordEncoder.matches(login.getPassword(), user.get().getPassword())) {
            user.get().setDerniereConnexion(LocalDateTime.now());
            return tokenService.createToken(userMapper.toDto(user.get()));
        }
        throw new BadCredentialsException("Login/mot de passe incorrect.");
    }
}
