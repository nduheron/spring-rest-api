package fr.nduheron.poc.springrestapi.user.dto.login;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;


public class Token implements Serializable {

    @JsonProperty("access_token")
    @Schema(example = "RsT5OjbzRn430zqMLgV3Ia", required = true)
    private String access;

    @JsonProperty("token_type")
    @Schema(example = "bearer", required = true)
    private String type;

    @JsonProperty("expires_in")
    @Schema(example = "3600", required = true)
    private long expiresIn;

    public Token(String access, long expiresIn) {
        this.access = access;
        this.expiresIn = expiresIn;
        this.type = "bearer";
    }

    public String getAccess() {
        return access;
    }

    public String getType() {
        return type;
    }

    public long getExpiresIn() {
        return expiresIn;
    }


}
