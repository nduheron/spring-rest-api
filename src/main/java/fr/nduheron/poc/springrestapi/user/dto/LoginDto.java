package fr.nduheron.poc.springrestapi.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@ApiModel("Login")
public class LoginDto {

    @NotNull
    @Size(min = 2, max = 20)
    @ApiModelProperty(example = "batman")
    private String username;

    @NotNull
    @Size(min = 5, max = 20)
    @ApiModelProperty(example = "12345")
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
