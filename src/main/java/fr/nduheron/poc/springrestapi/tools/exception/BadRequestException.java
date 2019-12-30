package fr.nduheron.poc.springrestapi.tools.exception;

import fr.nduheron.poc.springrestapi.tools.exception.model.Error;

import java.util.List;

/**
 * Exception métier. On utilise cette exception pour retournée une erreur 400.
 */
public class BadRequestException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private List<Error> errors;

    public BadRequestException(List<Error> errors) {
        this.errors = errors;
    }

    public List<Error> getErrors() {
        return errors;
    }

    public void setErrors(List<Error> errors) {
        this.errors = errors;
    }
}
