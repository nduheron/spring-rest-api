package fr.nduheron.poc.springrestapi.tools.rest.cache;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Créer un Etag et l'ajoute au header de la réponse
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Etag {

    /**
     * @return la durée (en seconde) pendant laquelle le client peut garder la réponse en cache sans rappeler le serveur (-1 = must-revalidate)
     */
    String maxAge() default "";

}
