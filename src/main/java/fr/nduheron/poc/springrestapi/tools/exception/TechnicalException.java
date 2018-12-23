package fr.nduheron.poc.springrestapi.tools.exception;

/**
 * Exception technique. On utilise cette exception pour retournée une erreur
 * 500.
 */
public class TechnicalException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public TechnicalException(String message, Throwable cause) {
        super(message, cause);
    }

}
