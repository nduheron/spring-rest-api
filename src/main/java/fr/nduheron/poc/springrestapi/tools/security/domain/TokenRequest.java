package fr.nduheron.poc.springrestapi.tools.security.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;


public class TokenRequest implements Serializable {

    @Pattern(regexp = "^password$")
    @Schema(defaultValue = "password")
    @JsonProperty("grant_type")
    private String grantType = "password";

    @NotNull
    @Size(min = 2, max = 20)
    @Schema(example = "batman")
    private String username;

    @NotNull
    @Size(min = 5, max = 20)
    @Schema(example = "12345")
    private String password;

    public String getGrantType() {
        return grantType;
    }

    public void setGrantType(String grantType) {
        this.grantType = grantType;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
