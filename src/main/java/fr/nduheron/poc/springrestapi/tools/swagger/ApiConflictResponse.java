package fr.nduheron.poc.springrestapi.tools.swagger;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Permet d'ajouter les erreurs fonctionnelles à la documentation swagger de
 * manière moins verbeuse que les annotation existantes.
 * 
 * <pre>
 * <code>
 * 		&#64;ApiResponses(value= { @ApiResponse(code=409, message="L'utilisateur existe déjà") })
 * </code>
 * devient :
 * <code>
 * 		&#64;ApiConflictResponse(message = "L'utilisateur existe déjà")
 * </code>
 * </pre>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiConflictResponse {

	/**
	 *
	 * @return le message à afficher
	 */
	String message();

}
