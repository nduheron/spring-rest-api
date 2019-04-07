package fr.nduheron.poc.springrestapi.tools.exception.mapper;

import fr.nduheron.poc.springrestapi.tools.exception.FunctionalException;
import fr.nduheron.poc.springrestapi.tools.exception.NotFoundException;
import fr.nduheron.poc.springrestapi.tools.exception.model.Error;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.persistence.EntityNotFoundException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.util.stream.Collectors.joining;

/**
 * Ce gestionnaire d'erreur permet de traiter les erreurs qui nécessitent un
 * traitement spécifique d'un point de vue REST. Par exemple, la gestion des
 * ressources non trouvées doivent remonter un status HTTP 404.
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
     * Gestion de l'exception {@link AccessDeniedException}
     */
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public void handleAccessDeniedException(final AccessDeniedException ex) {
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
    @ExceptionHandler({FunctionalException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public List<Error> handleFunctionalException(final FunctionalException ex) {
        String message = messageSource.getMessage(ex.getI18nKey(), ex.getArgs(), LocaleContextHolder.getLocale());
        return Collections.singletonList(new Error(ex.getCode(), message, ex.getAdditionalsInformations()));
    }

    /**
     * Gestion de l'exception {@link MethodArgumentNotValidException}
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public List<Error> handleMethodArgumentNotValidException(final MethodArgumentNotValidException ex) {
        List<Error> errors = new ArrayList<>(ex.getBindingResult().getAllErrors().size());

        for (ObjectError objectError : ex.getBindingResult().getAllErrors()) {
            FieldError fieldError = (FieldError) objectError;
            String[] path = StringUtils.splitPreserveAllTokens(fieldError.getField(), ".");
            errors.add(new Error(Error.INVALID_FORMAT, fieldError.getDefaultMessage(), path[path.length - 1]));
        }
        return errors;
    }


    /**
     * Gestion de l'exception {@link MethodArgumentTypeMismatchException}
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public List<Error> handleMethodArgumentTypeMismatchException(final MethodArgumentTypeMismatchException ex) {
        List<Error> errors;
        if (ex.getRequiredType().isEnum()) {
            errors = Collections.singletonList(new Error(Error.INVALID_FORMAT, "Allowable values: " + Arrays.stream(ex.getRequiredType().getEnumConstants()).map(Object::toString).collect(joining(", ")), ex.getName()));
        } else {
            errors = Collections.singletonList(new Error(Error.INVALID_FORMAT, ex.getRootCause().getMessage(), ex.getName()));
        }

        return errors;
    }

    /**
     * Gestion de l'exception {@link ConstraintViolationException}
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public List<Error> handleConstraintViolationException(final ConstraintViolationException ex) {
        List<Error> errors = new ArrayList<>(ex.getConstraintViolations().size());

        for (ConstraintViolation objectError : ex.getConstraintViolations()) {
            String[] path = StringUtils.splitPreserveAllTokens(objectError.getPropertyPath().toString(), ".");
            String attribute = path[path.length - 1];
            errors.add(new Error(Error.INVALID_FORMAT, objectError.getMessage(), attribute));
        }
        return errors;
    }

    /**
     * Gestion de l'exception {@link MissingServletRequestParameterException}
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public List<Error> handleMissingServletRequestParameterException(final MissingServletRequestParameterException ex) {
        return Collections.singletonList(new Error(Error.INVALID_FORMAT, ex.getMessage(), ex.getParameterName()));
    }


    /**
     * Gestion de l'exception {@link Exception}
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public void handleException(final Exception ex) {
        // on log simplement l'erreur, on ne retourne pas le message de l'exception car on ne veut pas exposer des messages d'erreurs techniques aux clients (ex: la requête SQL pour une SQLException)
        LOG.error("Une erreur inattendue s'est produite.", ex);
    }
}
