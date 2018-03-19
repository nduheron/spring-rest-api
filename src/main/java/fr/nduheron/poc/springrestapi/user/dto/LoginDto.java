package fr.nduheron.poc.springrestapi.user.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class LoginDto {

	@NotNull
	@Size(min = 2, max = 20)
	private String username;

	@NotNull
	@Size(min = 5, max = 20)
	private String password;

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

}
