package fr.nduheron.poc.springrestapi.user.controller;

import fr.nduheron.poc.springrestapi.tools.cache.Etag;
import fr.nduheron.poc.springrestapi.user.dto.ChangePasswordDto;
import fr.nduheron.poc.springrestapi.user.dto.UserDto;
import fr.nduheron.poc.springrestapi.user.model.User;
import fr.nduheron.poc.springrestapi.user.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;

import static fr.nduheron.poc.springrestapi.config.OpenApiConfiguration.DEFAULT_BAD_REQUEST;
import static fr.nduheron.poc.springrestapi.config.OpenApiConfiguration.OAUTH_PASSWORD_FLOW;

@RestController
@RequestMapping("/v1/accounts/me")
@Transactional
@Tag(name = "Profil utilisateur")
public class AccountController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository repo;

    @PutMapping("/attributes/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Changer de mot de passe", security = @SecurityRequirement(name = OAUTH_PASSWORD_FLOW))
    @ApiResponse(responseCode = "400", ref = DEFAULT_BAD_REQUEST)
    public void changePassword(@RequestBody @Valid final ChangePasswordDto changePassword) {
        UserDto userConnecte = (UserDto) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = repo.getOne(userConnecte.getLogin());
        if (passwordEncoder.matches(changePassword.getOldPassword(), user.getPassword())) {
            user.setPassword(passwordEncoder.encode(changePassword.getNewPassword()));
            repo.save(user);
        } else {
            throw new AccessDeniedException("Le mot de passe est invalide.");
        }
    }

    @GetMapping
    @Operation(summary = "Récupérer le profil de l'utilisateur connecté", security = @SecurityRequirement(name = OAUTH_PASSWORD_FLOW))
    @Etag(maxAge = "${accounts.me.maxAge}")
    public UserDto whoIAm() {
        return (UserDto) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

}
