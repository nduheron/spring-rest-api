package fr.nduheron.poc.springrestapi.tools.swagger;

import fr.nduheron.poc.springrestapi.tools.exception.model.Error;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Permet d'ajouter les erreurs 400 à la documentation swagger en précistant les codes erreurs possibles
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiBadRequestResponse {


    /**
     * @return la liste des codes d'erreurs possibles
     */
    ErrorDocumentation[] value() default {@ErrorDocumentation(code = Error.REQUIRED), @ErrorDocumentation(code = Error.INVALID_PARAMETER)};

}
