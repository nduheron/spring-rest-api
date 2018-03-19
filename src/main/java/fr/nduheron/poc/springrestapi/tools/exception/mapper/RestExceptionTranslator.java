package fr.nduheron.poc.springrestapi.tools.exception.mapper;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import fr.nduheron.poc.springrestapi.tools.exception.FunctionalException;
import fr.nduheron.poc.springrestapi.tools.exception.NotFoundException;
import fr.nduheron.poc.springrestapi.tools.exception.model.ErrorParameter;
import fr.nduheron.poc.springrestapi.tools.exception.model.FunctionalError;

/**
 * Ce gestionnaire d'erreur permet de traiter les erreurs qui nécessitent un
 * traitement spécifique d'un point de vue REST. Par exemple, la gestion des
 * ressources non trouvées doivent remonter un status HTTP 404.
 *
 */
@ControllerAdvice
@ResponseBody
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RestExceptionTranslator {

	private static final Logger LOG = LoggerFactory.getLogger(RestExceptionTranslator.class);

	@Autowired
	private MessageSource messageSource;

	/**
	 * Gestion de l'exception {@link EntityNotFoundException}
	 */
	@ExceptionHandler(EntityNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public void handleEntityNotFoundException(final EntityNotFoundException ex) {
		LOG.warn(ex.getMessage());
	}

	/**
	 * Gestion de l'exception {@link NotFoundException}
	 */
	@ExceptionHandler(NotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public void handleNotFoundException(final NotFoundException ex) {
		LOG.warn(ex.getMessage());
	}

	/**
	 * Gestion de l'exception {@link AuthenticationException}
	 */
	@ExceptionHandler(AuthenticationException.class)
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	public void handleAuthenticationException(final AuthenticationException ex) {
		LOG.warn(ex.getMessage());
	}

	/**
	 * Gestion des erreurs fonctionnelles {@link FunctionalException}
	 */
	@ExceptionHandler({ FunctionalException.class })
	@ResponseStatus(HttpStatus.CONFLICT)
	public FunctionalError handleFunctionalException(final FunctionalException ex) {
		String message = messageSource.getMessage(ex.getI18nKey(), ex.getArgs(), LocaleContextHolder.getLocale());
		return new FunctionalError(message, ex.getCode());
	}

	/**
	 * Gestion de l'exception {@link MethodArgumentNotValidException}
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public List<ErrorParameter> handleMethodArgumentNotValidException(final MethodArgumentNotValidException ex) {
		List<ErrorParameter> errors = new ArrayList<>(ex.getBindingResult().getAllErrors().size());
		for (ObjectError objectError : ex.getBindingResult().getAllErrors()) {
			FieldError fieldError = (FieldError) objectError;
			errors.add(new ErrorParameter(fieldError.getField(), fieldError.getDefaultMessage()));
		}
		return errors;
	}

}
