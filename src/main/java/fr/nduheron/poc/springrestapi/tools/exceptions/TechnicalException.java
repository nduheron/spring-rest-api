package fr.nduheron.poc.springrestapi.tools.exceptions;

/**
 * Exception technique. On utilise cette exception pour retournée une erreur 500.
 */
public class TechnicalException extends RuntimeException {

    public TechnicalException(String message, Throwable cause) {
        super(message, cause);
    }

}
