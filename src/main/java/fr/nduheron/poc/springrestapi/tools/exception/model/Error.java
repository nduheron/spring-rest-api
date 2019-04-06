package fr.nduheron.poc.springrestapi.tools.exception.model;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;

public class Error {
    public static final String INVALID_FORMAT = "INVALID_FORMAT";

    @NotBlank
    @ApiModelProperty(value = "le code de l'erreur", required = true)
    private String code;

    @NotBlank
    @ApiModelProperty(value = "description de l'erreur", required = true)
    private String message;

    @ApiModelProperty(value = "nom de l'attribut source de l'erreur")
    private String attribute;

    @ApiModelProperty(value = "propriétés additionnelles spécifiques")
    private Object additionalsInformations;

    public Error() {
        super();
    }

    public Error(String code, String message, Object additionalsInformations) {
        this.code = code;
        this.message = message;
        this.additionalsInformations = additionalsInformations;
    }

    public Error(String code, String message, String attribute) {
        this.code = code;
        this.message = message;
        this.attribute = attribute;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getAdditionalsInformations() {
        return additionalsInformations;
    }

    public void setAdditionalsInformations(Object additionalsInformations) {
        this.additionalsInformations = additionalsInformations;
    }
}
