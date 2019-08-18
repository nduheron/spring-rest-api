package fr.nduheron.poc.springrestapi.tools.exception.model;


import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;

public class Error {
    public static final String INVALID_FORMAT = "INVALID_FORMAT";

    @NotBlank
    @Schema(description = "le code de l'erreur")
    private String code;

    @NotBlank
    @Schema(description = "description de l'erreur")
    private String message;

    @Schema(description = "nom de l'attribut source de l'erreur")
    private String attribute;

    @Schema(description = "propriétés additionnelles spécifiques")
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
