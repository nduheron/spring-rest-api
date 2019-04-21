package fr.nduheron.poc.springrestapi.user.controller;

import fr.nduheron.poc.springrestapi.tools.exception.model.Error;
import fr.nduheron.poc.springrestapi.tools.security.domain.Token;
import fr.nduheron.poc.springrestapi.tools.security.service.TokenService;
import fr.nduheron.poc.springrestapi.user.mapper.UserMapper;
import fr.nduheron.poc.springrestapi.user.model.User;
import fr.nduheron.poc.springrestapi.user.repository.UserRepository;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/v1/oauth")
@Transactional
@Api(tags = "Authentification")
@Validated
public class AuthentificationController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository repo;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UserMapper userMapper;

    @PostMapping(value = "/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @ApiResponses(
            @ApiResponse(code = 400, message = "Il y a une(des) erreur(s) dans la requête.", response = Error.class, responseContainer = "list", examples = @Example({
                    @ExampleProperty(mediaType = "InvalidFormat", value = "[{\"code\": \"INVALID_FORMAT\", \"message\": \"may not be null\", \"attribute\": \"username\"},{\"code\": \"INVALID_FORMAT\", \"message\": \"must be greater than or equal to 2\", \"attribute\": \"password\"}]"),
            }))
    )
    public Token login(
            @RequestParam("username") @Size(min = 2, max = 20) @ApiParam(example = "batman", required = true) String username,
            @RequestParam("password") @Size(min = 5, max = 20) @ApiParam(example = "12345", required = true) String password) {
        Optional<User> user = repo.findById(username);
        if (!user.isPresent()) {
            throw new UsernameNotFoundException(String.format("L'utilisateur %s n'existe pas.", username));
        }

        if (!user.get().isEnabled()) {
            throw new DisabledException(String.format("L'utilisateur %s n'est pas actif.", username));
        }

        if (passwordEncoder.matches(password, user.get().getPassword())) {
            user.get().setDerniereConnexion(LocalDateTime.now());
            return tokenService.createToken(userMapper.toDto(user.get()));
        }
        throw new BadCredentialsException("Login/mot de passe incorrect.");
    }
}
