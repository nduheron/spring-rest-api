package fr.nduheron.poc.springrestapi.user.dto;

import com.opencsv.bean.CsvBindByPosition;
import com.opencsv.bean.CsvCustomBindByPosition;
import fr.nduheron.poc.springrestapi.user.model.Role;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

public abstract class AbstractUserDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    @Email
    @Size(max = 128)
    @Schema(example = "batman@yopmail.fr")
    @CsvBindByPosition(position = 3)
    private String email;

    @NotNull
    @Size(min = 2, max = 50)
    @Schema(example = "Wayne")
    @CsvBindByPosition(position = 1)
    private String nom;

    @NotNull
    @Size(min = 2, max = 50)
    @Schema(example = "Bruce")
    @CsvBindByPosition(position = 2)
    private String prenom;

    @NotNull
    @CsvCustomBindByPosition(position = 4, converter = RoleBeanField.class)
    private Role role;

    private boolean enabled;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

}
