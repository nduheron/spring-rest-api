package fr.nduheron.poc.springrestapi.tools.rest.errors;

import com.fasterxml.jackson.databind.JsonMappingException;
import fr.nduheron.poc.springrestapi.tools.exceptions.FunctionalException;
import fr.nduheron.poc.springrestapi.tools.exceptions.NotFoundException;
import fr.nduheron.poc.springrestapi.tools.exceptions.TechnicalException;
import fr.nduheron.poc.springrestapi.tools.rest.errors.dto.AttributeErrorDto;
import fr.nduheron.poc.springrestapi.tools.rest.errors.dto.FunctionalErrorDto;
import fr.nduheron.poc.springrestapi.tools.rest.errors.mapper.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.validation.ConstraintViolationException;
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

    private static final List<BadRequestExceptionMapper> BAD_REQUEST_EXCEPTION_MAPPERS = Arrays.asList(
            new ConstraintViolationExceptionMapper(),
            new InvalidJsonExceptionMapper(),
            new MethodArgumentNotValidExceptionMapper(),
            new MethodArgumentTypeMismatchExceptionMapper(),
            new MissingRequestHeaderExceptionMapper(),
            new MissingServletRequestParameterExceptionMapper()
    );

    private final MessageSource messageSource;

    public RestExceptionTranslator(MessageSource messageSource) {
        this.messageSource = messageSource;
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
    @ResponseStatus(HttpStatus.CONFLICT)
    public FunctionalErrorDto handleFunctionalException(final FunctionalException ex) {
        LOG.warn(ex.getMessage());
        String message = messageSource.getMessage(ex.getI18nKey(), ex.getArgs(), LocaleContextHolder.getLocale());
        return new FunctionalErrorDto(ex.getCode(), message, ex.getAdditionalData());
    }

    /**
     * Gestion des erreurs 400
     */
    @ExceptionHandler({
            ConstraintViolationException.class,
            JsonMappingException.class,
            HttpMessageNotReadableException.class,
            MethodArgumentNotValidException.class,
            MethodArgumentTypeMismatchException.class,
            MissingRequestHeaderException.class,
            MissingServletRequestParameterException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public List<AttributeErrorDto> handleBadRequestException(final Exception ex) {
        LOG.warn(ex.getMessage());
        return BAD_REQUEST_EXCEPTION_MAPPERS
                .stream()
                .filter(it -> it.canMap(ex))
                .map(it -> it.map(ex))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(String.format("Mapper not found for exception %s", ex.getClass())));
    }

    /**
     * Gestion de l'exception {@link Exception}
     */
    @ExceptionHandler({Exception.class, TechnicalException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public void handleException(final Exception ex) {
        // on log simplement l'erreur, on ne retourne pas le message de l'exception car on ne veut pas exposer des messages d'erreurs techniques aux clients (ex: la requête SQL pour une SQLException)
        LOG.error("An error occurred", ex);
    }
}
