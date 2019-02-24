package fr.nduheron.poc.springrestapi.tools.swagger;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Permet de documenter une erreur
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface ErrorDocumentation {


    /**
     * @return le code erreur
     */
    String code();

    /**
     * @return le type pour le champ additionalsInformations
     */
    Class<?> additionalsInformationsType() default Void.class;


}
