package fr.nduheron.poc.springrestapi.user.controller;

import fr.nduheron.poc.springrestapi.tools.openapi.annotations.ApiDefaultResponse400;
import fr.nduheron.poc.springrestapi.tools.security.domain.Token;
import fr.nduheron.poc.springrestapi.tools.security.domain.TokenRequest;
import fr.nduheron.poc.springrestapi.tools.security.service.TokenService;
import fr.nduheron.poc.springrestapi.user.mapper.UserMapper;
import fr.nduheron.poc.springrestapi.user.model.User;
import fr.nduheron.poc.springrestapi.user.repository.UserRepository;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
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

@RestController
@RequestMapping("/v1/oauth")
@Transactional
@Validated
@Tag(name = "Authentification")
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
    @ApiDefaultResponse400
    @ApiResponse(responseCode = "401", ref = "401")
    @ApiResponse(responseCode = "500", ref = "500")
    public Token login(@RequestBody @Valid TokenRequest tokenRequest) {

        Optional<User> user = repo.findById(tokenRequest.getUsername());
        if (!user.isPresent()) {
            throw new UsernameNotFoundException(String.format("L'utilisateur %s n'existe pas.", tokenRequest.getUsername()));
        }

        if (!user.get().isEnabled()) {
            throw new DisabledException(String.format("L'utilisateur %s n'est pas actif.", tokenRequest.getUsername()));
        }

        if (passwordEncoder.matches(tokenRequest.getPassword(), user.get().getPassword())) {
            user.get().setDerniereConnexion(LocalDateTime.now());
            return tokenService.createToken(userMapper.toDto(user.get()));
        }
        throw new BadCredentialsException("Login/mot de passe incorrect.");
    }
}
