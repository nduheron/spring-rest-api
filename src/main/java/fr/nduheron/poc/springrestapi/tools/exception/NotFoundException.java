package fr.nduheron.poc.springrestapi.tools.exception;

/**
 * Exception utilisée quand une resource n'existe pas. Le message est utilisé
 * pour les logs.
 */
public class NotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public NotFoundException(String message) {
        super(message);
    }

}
