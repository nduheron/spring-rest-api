package fr.nduheron.poc.springrestapi.user.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import fr.nduheron.poc.springrestapi.user.model.Role;
import io.swagger.annotations.ApiModelProperty;

public abstract class AbstractUserDto {

	@NotNull
	@Email
	@Size(max = 128)
	private String email;

	@NotNull
	@Size(min = 2, max = 50)
	private String nom;

	@NotNull
	@Size(min = 2, max = 50)
	private String prenom;

	@NotNull
	@ApiModelProperty(required = true)
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
