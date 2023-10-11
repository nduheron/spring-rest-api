package fr.nduheron.poc.springrestapi.user.model;

import fr.nduheron.poc.springrestapi.tools.exceptions.FunctionalException;

public class ExistException extends FunctionalException {

    public static final String ALREADY_EXIST = "ALREADY_EXIST";

    public ExistException(String message, String i18nKey, String... args) {
        super(message, i18nKey, args);
    }

    @Override
    public String getCode() {
        return ALREADY_EXIST;
    }

}
