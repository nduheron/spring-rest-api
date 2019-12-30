package fr.nduheron.poc.springrestapi.security;

import fr.nduheron.poc.springrestapi.dto.UserDto;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Arrays;

/**
 * Service métier pour la sécurité.
 */
@Service
public class SecurityService {

    /**
     * Vérfie si l'utilisateur connecté à au moins 1 des rôles en paramètre
     */
    public boolean hasRole(UserDto.RoleEnum... roles) {
        UserDto user = (UserDto) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return Arrays.stream(roles).anyMatch(r1 -> r1 == user.getRole());
    }

    /**
     * @return le login de l'utilisateur connecté
     */
    public String getLogin() {
        UserDto user = (UserDto) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return user.getLogin();
    }

    /**
     * Valide que l'utilisateur connecté à accès seulement à ses données, ou à
     * toutes les données si il est ADMIN.
     */
    public boolean isUserAuthorized(String login) {
        return hasRole(UserDto.RoleEnum.ADMIN) || getLogin().equals(login);
    }
}
