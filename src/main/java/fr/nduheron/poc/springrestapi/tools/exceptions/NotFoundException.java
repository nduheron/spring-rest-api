package fr.nduheron.poc.springrestapi.tools.exceptions;

/**
 * Exception utilisée quand une resource n'existe pas. Le message est utilisé
 * pour les logs.
 */
public class NotFoundException extends Exception {

    public NotFoundException(String message) {
        super(message);
    }

}
