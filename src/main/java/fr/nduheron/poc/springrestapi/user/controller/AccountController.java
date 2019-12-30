package fr.nduheron.poc.springrestapi.user.controller;

import fr.nduheron.poc.springrestapi.api.ProfilUtilisateurApi;
import fr.nduheron.poc.springrestapi.dto.ChangePasswordDto;
import fr.nduheron.poc.springrestapi.dto.UserDto;
import fr.nduheron.poc.springrestapi.tools.cache.Etag;
import fr.nduheron.poc.springrestapi.user.model.User;
import fr.nduheron.poc.springrestapi.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import javax.validation.Valid;

@RestController
@Transactional
public class AccountController implements ProfilUtilisateurApi {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository repo;


    public ResponseEntity<Void> changePassword(@RequestBody @Valid final ChangePasswordDto changePassword) {
        UserDto userConnecte = (UserDto) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = repo.getOne(userConnecte.getLogin());
        if (passwordEncoder.matches(changePassword.getOldPassword(), user.getPassword())) {
            user.setPassword(passwordEncoder.encode(changePassword.getNewPassword()));
            repo.save(user);
        } else {
            throw new AccessDeniedException("Le mot de passe est invalide.");
        }
        return ResponseEntity.noContent().build();
    }


    @Etag(maxAge = 60)
    public ResponseEntity<UserDto> whoIAm() {
        return ResponseEntity.ok((UserDto) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    }

}
