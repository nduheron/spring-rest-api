package fr.nduheron.poc.springrestapi.user.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class CreateUserDto extends AbstractUserDto {

	private static final long serialVersionUID = 1L;

	@NotNull
	@Size(min = 2, max = 20)
	private String login;

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

}
