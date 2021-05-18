package fr.nduheron.poc.springrestapi.tools.exception;

import java.io.Serializable;

/**
 * Exception métier. On utilise cette exception pour retournée une erreur 409.
 */
public abstract class FunctionalException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * La clé i18n permettant au client de recevoir un message dans la langue
     * souhaitée
     */
    private final String i18nKey;

    /**
     * Les arguments du message d'erreur
     */
    private final String[] args;

    private Serializable additionalsInformations;

    protected FunctionalException(String i18nKey, String... args) {
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

    public Serializable getAdditionalsInformations() {
        return additionalsInformations;
    }

    public void setAdditionalsInformations(Serializable additionalsInformations) {
        this.additionalsInformations = additionalsInformations;
    }
}
