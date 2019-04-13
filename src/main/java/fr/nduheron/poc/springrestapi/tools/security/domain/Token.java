package fr.nduheron.poc.springrestapi.tools.security.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

public class Token implements Serializable {

    @JsonProperty("access_token")
    @ApiModelProperty(example = "RsT5OjbzRn430zqMLgV3Ia", required = true)
    private String access;

    @JsonProperty("token_type")
    @ApiModelProperty(example = "bearer", required = true)
    private String type;

    @JsonProperty("expires_in")
    @ApiModelProperty(example = "3600", required = true)
    private long expiresIn;

    public Token() {
    }

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
