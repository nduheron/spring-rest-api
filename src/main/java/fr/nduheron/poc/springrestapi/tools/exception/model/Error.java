package fr.nduheron.poc.springrestapi.tools.exception.model;

import io.swagger.annotations.ApiModelProperty;

public class Error {
    public static final String REQUIRED = "REQUIRED";
    public static final String INVALID_PARAMETER = "INVALID_PARAMETER";

    /**
     * Le nom du parametre en erreur
     */
    @ApiModelProperty(required = true, example = "REQUIRED")
    private String code;

    /**
     * Le message d'erreur
     */
    @ApiModelProperty(required = true, example = "ne doit pas être nul")
    private String message;

    @ApiModelProperty(notes = "le nom du champ en erreur", example = "login")
    private String name;

    @ApiModelProperty(notes = "le chemin au format jsonpath", example = "$.user.login")
    private String path;

    @ApiModelProperty(notes = "Informations complémentaires à l'erreur")
    private Object additionalsInformations;

    public Error() {
        super();
    }

    public Error(String code, String message, Object additionalsInformations) {
        this.code = code;
        this.message = message;
        this.additionalsInformations = additionalsInformations;
    }

    public Error(String code, String message, String name, String path) {
        this.code = code;
        this.message = message;
        this.name = name;
        this.path = path;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
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
