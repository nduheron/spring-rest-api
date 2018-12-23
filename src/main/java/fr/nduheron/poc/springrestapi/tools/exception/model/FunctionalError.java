package fr.nduheron.poc.springrestapi.tools.exception.model;

import java.io.Serializable;

/**
 * Représente une erreur fonctionnelle
 */
public class FunctionalError implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Code permettant au client d'identifier le type d'erreur fonctionnelle
     */
    private String code;

    /**
     * Le message d'erreur
     */
    private String message;

    public FunctionalError() {
        super();
    }

    public FunctionalError(String message, String code) {
        super();
        this.code = code;
        this.message = message;
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

}
