package fr.nduheron.poc.springrestapi.tools.exceptions;

import java.io.Serializable;

/**
 * Exception métier
 */
public abstract class FunctionalException extends Exception {

    /**
     * La clé i18n permettant au client de recevoir un message dans la langue souhaitée
     */
    private final String i18nKey;

    /**
     * Les arguments du message d'erreur
     */
    private final String[] args;

    private Object additionalData;

    protected FunctionalException(String message, String i18nKey, String... args) {
        super(message);
        this.i18nKey = i18nKey;
        this.args = args;
    }


    public String getI18nKey() {
        return i18nKey;
    }

    public Object[] getArgs() {
        return args;
    }

    /**
     * @return le code permettant au client d'identifier le type d'erreur métier
     */
    public abstract String getCode();

    public Object getAdditionalData() {
        return additionalData;
    }

    public void setAdditionalData(Serializable additionalData) {
        this.additionalData = additionalData;
    }
}
