package fr.nduheron.poc.springrestapi.user.dto;

import java.time.LocalDateTime;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class UserDto extends AbstractUserDto {

	@NotNull
	@Size(min = 2, max = 20)
	private String login;

	private LocalDateTime derniereConnexion;

	public LocalDateTime getDerniereConnexion() {
		return derniereConnexion;
	}

	public void setDerniereConnexion(LocalDateTime derniereConnexion) {
		this.derniereConnexion = derniereConnexion;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

}
