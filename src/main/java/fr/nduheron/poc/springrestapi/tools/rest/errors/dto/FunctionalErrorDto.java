package fr.nduheron.poc.springrestapi.tools.rest.errors.dto;


import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;

@Schema(name = "FunctionalError")
public class FunctionalErrorDto {
    public static final String INVALID_FORMAT = "INVALID_FORMAT";

    @NotBlank
    @Schema(description = "le code de l'erreur")
    private String code;

    @NotBlank
    @Schema(description = "description de l'erreur")
    private String message;

    @Schema(description = "propriétés additionnelles spécifiques")
    private Object additionalData;

    public FunctionalErrorDto() {
        super();
    }

    public FunctionalErrorDto(String code, String message, Object additionalData) {
        this.code = code;
        this.message = message;
        this.additionalData = additionalData;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getAdditionalData() {
        return additionalData;
    }

    public void setAdditionalData(Object additionalData) {
        this.additionalData = additionalData;
    }
}
