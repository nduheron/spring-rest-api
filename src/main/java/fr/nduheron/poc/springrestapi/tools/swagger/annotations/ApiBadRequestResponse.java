package fr.nduheron.poc.springrestapi.tools.swagger.annotations;

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
     * @return la liste des exemples de réponses possibles
     */
    ErrorExample[] value();

}
