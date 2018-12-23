package fr.nduheron.poc.springrestapi.tools.exception.model;

import java.io.Serializable;

/**
 * Représente un paramètre de requête en erreur
 */
public class ErrorParameter implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Le nom du parametre en erreur
     */
    private String field;

    /**
     * Le message d'erreur
     */
    private String message;

    public ErrorParameter() {
        super();
    }

    public ErrorParameter(String field, String message) {
        super();
        this.field = field;
        this.message = message;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
