package fr.nduheron.poc.springrestapi.user.dto;

import com.opencsv.bean.CsvBindByPosition;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@ApiModel("User")
public class UserDto extends AbstractUserDto {

    private static final long serialVersionUID = 1L;

	@NotNull
	@Size(min = 2, max = 20)
    @CsvBindByPosition(position = 0)
    @ApiModelProperty(example = "batman")
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
