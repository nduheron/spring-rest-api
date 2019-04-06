package fr.nduheron.poc.springrestapi.tools.swagger.annotations;

import org.apache.commons.lang3.StringUtils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Permet de documenter une erreur
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ErrorExample {

    /**
     * @return le code erreur
     */
    String code();

    /**
     * Le message d'erreur
     */
    String message();

    /**
     * nom de l'attribut source de l'erreur
     */
    String attribute() default StringUtils.EMPTY;


    /**
     * @return le type pour le champ additionalsInformations
     */
    Class<?> additionalsInformationsType() default Void.class;

}

