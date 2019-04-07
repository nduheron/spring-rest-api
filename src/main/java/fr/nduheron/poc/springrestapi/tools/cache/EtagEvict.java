package fr.nduheron.poc.springrestapi.tools.cache;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Supprime un Etag du cache pour force la mise à jour de la resource côté client
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EtagEvict {

    /**
     * @return le nom du cache dans lequel récupérer la donnée
     */
    String cache();

    /**
     * @return true si l'on doit également supprimer la ressource parente du cache
     */
    boolean evictParentResource() default false;

    /**
     * @return true si l'on doit également supprimer les ressources filles du cache
     */
    boolean evictChildResources() default false;

}
