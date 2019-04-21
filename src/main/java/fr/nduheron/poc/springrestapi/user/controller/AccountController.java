package fr.nduheron.poc.springrestapi.user.controller;

import fr.nduheron.poc.springrestapi.tools.cache.Etag;
import fr.nduheron.poc.springrestapi.tools.exception.model.Error;
import fr.nduheron.poc.springrestapi.user.dto.ChangePasswordDto;
import fr.nduheron.poc.springrestapi.user.dto.UserDto;
import fr.nduheron.poc.springrestapi.user.model.User;
import fr.nduheron.poc.springrestapi.user.repository.UserRepository;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;

@RestController
@RequestMapping("/v1/accounts/me")
@Transactional
@Api(tags = "Profil utilisateur")
public class AccountController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository repo;


    @PutMapping("/attributes/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiOperation(value = "Changer de mot de passe")
    @ApiResponses(
            @ApiResponse(code = 400, message = "Il y a une(des) erreur(s) dans la requête.", response = Error.class, responseContainer = "list", examples = @Example(
                    @ExampleProperty(mediaType = "InvalidFormat", value = "[{\"code\": \"INVALID_FORMAT\",\"message\": \"may not be null\",\"attribute\": \"newPassword\"},{\"code\": \"INVALID_FORMAT\",\"message\": \"must be greater than or equal to 2\",\"attribute\": \"oldPassword\"}]")
            ))
    )
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
    @ApiOperation(value = "Récupérer le profil de l'utilisateur connecté")
    @Etag(maxAge = 60)
    public UserDto find() {
        return (UserDto) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

}
