package fr.nduheron.poc.springrestapi.tools.rest.errors.dto;


import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;

@Schema(name = "AttributeError")
public class AttributeErrorDto {
    @NotBlank
    private String message;

    private String path;

    private String attribute;

    public AttributeErrorDto() {
        super();
    }

    public AttributeErrorDto(String message, String path, String attribute) {
        this.message = message;
        this.path = path;
        this.attribute = attribute;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
