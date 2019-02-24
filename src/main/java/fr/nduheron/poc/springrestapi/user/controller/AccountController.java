package fr.nduheron.poc.springrestapi.user.controller;

import fr.nduheron.poc.springrestapi.tools.swagger.ApiBadRequestResponse;
import fr.nduheron.poc.springrestapi.user.dto.ChangePasswordDto;
import fr.nduheron.poc.springrestapi.user.dto.UserDto;
import fr.nduheron.poc.springrestapi.user.model.User;
import fr.nduheron.poc.springrestapi.user.repository.UserRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;

@RestController
@Transactional
@Api(tags = "Profil utilisateur")
public class AccountController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository repo;

    @PutMapping("/v1/account/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiOperation(value = "Changer de mot de passe")
    @Deprecated
    @ApiBadRequestResponse
    public void changePassword(Authentication authentication,
                               @RequestBody @Valid final ChangePasswordDto changePassword) {
        changePasswordV2(authentication, changePassword);
    }

    @PatchMapping("/v2/account/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiOperation(value = "Changer de mot de passe")
    @ApiBadRequestResponse
    public void changePasswordV2(Authentication authentication,
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

    @GetMapping("/v1/account")
    @ApiOperation(value = "Récupérer le profil de l'utilisateur conneté")
    public UserDto find(Authentication authentication) {
        return (UserDto) authentication.getPrincipal();
    }

}
