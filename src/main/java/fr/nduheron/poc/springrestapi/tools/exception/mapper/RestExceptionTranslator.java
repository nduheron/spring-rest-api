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
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.persistence.EntityNotFoundException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    private static final List<String> REQUIRED_CODES = Arrays.asList("NotNull", "NotEmpty", "NotBlank");

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
    @ExceptionHandler({FunctionalException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public List<Error> handleFunctionalException(final FunctionalException ex) {
        String message = messageSource.getMessage(ex.getI18nKey(), ex.getArgs(), LocaleContextHolder.getLocale());
        return Arrays.asList(new Error(ex.getCode(), message, ex.getAdditionalsInformations()));
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
            errors.add(new Error(getErrorCode(fieldError), fieldError.getDefaultMessage(), path[path.length - 1], "$." + fieldError.getField()));
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
            String name = path[path.length - 1];
            errors.add(new Error(Error.INVALID_PARAMETER, objectError.getMessage(), name, "$." + name));
        }
        return errors;
    }

    /**
     * Gestion de l'exception {@link MissingServletRequestParameterException}
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public List<Error> handleMissingServletRequestParameterException(final MissingServletRequestParameterException ex) {
        return Arrays.asList(new Error(Error.REQUIRED, ex.getMessage(), ex.getParameterName(), "$." + ex.getParameterName()));
    }


    private String getErrorCode(FieldError fieldError) {
        return REQUIRED_CODES.contains(fieldError.getCode()) ? Error.REQUIRED : Error.INVALID_PARAMETER;
    }
}
