package fr.nduheron.poc.springrestapi.tools.openapi;

import fr.nduheron.poc.springrestapi.tools.log.LoggingCacheErrorHandler;
import io.swagger.v3.core.util.ReflectionUtils;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.core.ResponseBuilder;
import org.springdoc.core.SecurityParser;
import org.springdoc.core.SpringDocAnnotationsUtils;
import org.springdoc.core.customizers.OpenApiCustomiser;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.HandlerMethod;

import javax.annotation.security.RolesAllowed;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Component
@Primary
public class CustomResponseBuilder implements OpenApiCustomiser {
    private static final Logger logger = LoggerFactory.getLogger(CustomResponseBuilder.class);

    @Autowired
    private SecurityParser securityParser;

    @Override
    public ApiResponses build(Components components, HandlerMethod handlerMethod, Operation operation, String[] methodProduces) {
        if (methodProduces.length == 0) {
            // on produit du json par défaut
            methodProduces = new String[]{MediaType.APPLICATION_JSON_VALUE};
        }

        ApiResponses apiResponses = Optional.ofNullable(operation.getResponses())
                .orElse(new ApiResponses());
        Method method = handlerMethod.getMethod();

        // ajoute la documentation des annotations @ApiResponse et @ApiResponses
        getApiResponseDocumentation(method, components, methodProduces)
                .forEach(apiResponses::putIfAbsent);

        // ajoute la documentation générique en cas de succès
        addGenericSuccessfulResponse(components, methodProduces, apiResponses, method);

        // ajoute les erreures génériques seulement si l'annotation @ApiResponses n'est pas présente sur la méthode
        addGenericErrorsResponse(components, apiResponses, method);

        cleanApiResponses(apiResponses);

        return apiResponses;
    }

    @Override
    public void buildGenericResponse(Components components, Map<String, Object> findControllerAdvice) {
        findControllerAdvice.values().stream()
                .flatMap(controller -> stream(getClass(controller).getDeclaredMethods()))
                .filter(method -> method.isAnnotationPresent(ExceptionHandler.class))
                .forEach(method -> {
                    ResponseStatus responseStatus = AnnotatedElementUtils.findMergedAnnotation(method, ResponseStatus.class);
                    RequestMapping reqMappingMethod = ReflectionUtils.getAnnotation(method, RequestMapping.class);
                    String[] methodProduces = new String[0];
                    if (reqMappingMethod != null) {
                        methodProduces = reqMappingMethod.produces();
                    }
                    ApiResponse apiResponse = new ApiResponse()
                            .description(responseStatus.value().getReasonPhrase())
                            .content(super.buildContent(components, method, methodProduces));
                    components.addResponses(String.valueOf(responseStatus.code().value()), apiResponse);
                });
    }

    private void addGenericErrorsResponse(Components components, ApiResponses apiResponses, Method method) {
        if (ReflectionUtils.getAnnotation(method, io.swagger.v3.oas.annotations.responses.ApiResponses.class) == null) {
            components.getResponses().forEach((k, v) -> {
                if (!apiResponses.containsKey(k)) {
                    switch (k) {
                        case "404":
                            long cptPathVariables = stream(method.getParameterAnnotations()).flatMap(Arrays::stream).filter(annotation -> annotation.annotationType().equals(PathVariable.class)).count();
                            RequestMapping reqMappingMethod = ReflectionUtils.getAnnotation(method, RequestMapping.class);
                            if (cptPathVariables > 1 || (cptPathVariables == 1 && Arrays.stream(reqMappingMethod.method()).noneMatch(httpMethod -> httpMethod == RequestMethod.DELETE))) {
                                apiResponses.addApiResponse(k, new ApiResponse().$ref(k));
                            }
                            break;
                        case "401":
                            Optional.ofNullable(ReflectionUtils.getAnnotation(method, io.swagger.v3.oas.annotations.Operation.class))
                                    .ifPresent(operation -> securityParser
                                            .getSecurityRequirements(operation.security())
                                            .ifPresent(security -> apiResponses.addApiResponse(k, new ApiResponse().$ref(k))));
                            break;
                        case "403":
                            if (AnnotatedElementUtils.findMergedAnnotation(method, PreAuthorize.class) != null || AnnotatedElementUtils.findMergedAnnotation(method, RolesAllowed.class) != null) {
                                apiResponses.addApiResponse(k, new ApiResponse().$ref(k));
                            }
                            break;
                        case "500":
                            apiResponses.addApiResponse(k, new ApiResponse().$ref(k));
                            break;
                        default:
                            break;
                    }
                }
            });
        }
    }

    private void addGenericSuccessfulResponse(Components components, String[] methodProduces, ApiResponses apiResponses, Method method) {
        String successHttpCode = Optional.ofNullable(AnnotatedElementUtils.findMergedAnnotation(method, ResponseStatus.class))
                .map(annotation -> String.valueOf(annotation.code().value()))
                .orElse(String.valueOf(HttpStatus.OK.value()));
        if (!apiResponses.containsKey(successHttpCode)) {
            ApiResponse apiResponse = new ApiResponse();
            Content content = super.buildContent(components, method, methodProduces);
            apiResponse.setContent(content);
            apiResponses.addApiResponse(successHttpCode, apiResponse);
        }
    }

    private void cleanApiResponses(ApiResponses apiResponses) {
        apiResponses.forEach((s, apiResponse) -> {
            // on supprime les refs vides pour avoir un json valide
            if (StringUtils.isBlank(apiResponse.get$ref())) {
                apiResponse.set$ref(null);
            }
            if (StringUtils.isBlank(apiResponse.getDescription()) && StringUtils.isBlank(apiResponse.get$ref())) {
                try {
                    // on ajoute une description par défaut si il n'y en a pas
                    apiResponse.setDescription(HttpStatus.valueOf(Integer.parseInt(s)).getReasonPhrase());
                } catch (Exception e) {
                    logger.warn("Erreur lors de la récupération du status code de la réponse.");
                }
            }
        });
    }


    private Class<?> getClass(Object object) {
        if (AopUtils.isAopProxy(object)) {
            return AopUtils.getTargetClass(object);
        }
        return object.getClass();
    }

    /**
     * @return la liste des réponses d'une méthode d'un controller
     */
    private ApiResponses getApiResponseDocumentation(Method method, Components components, String[] methodProduces) {
        List<io.swagger.v3.oas.annotations.responses.ApiResponse> apiResponseAnnotations = Optional.ofNullable(ReflectionUtils.getAnnotation(method, io.swagger.v3.oas.annotations.responses.ApiResponses.class))
                .map(responses -> stream(responses.value()).collect(Collectors.toList()))
                .orElse(ReflectionUtils.getRepeatableAnnotations(method, io.swagger.v3.oas.annotations.responses.ApiResponse.class));

        ApiResponses apiResponses = new ApiResponses();

        apiResponseAnnotations.forEach(annotation -> {
            ApiResponse apiResponse = new ApiResponse();
            apiResponse.setDescription(annotation.description());
            io.swagger.v3.oas.annotations.media.Content[] contentdoc = annotation.content();
            SpringDocAnnotationsUtils.getContent(contentdoc, new String[0], methodProduces == null ? new String[0] : methodProduces, null, components, null)
                    .ifPresent(apiResponse::content);
            // fix : SpringDocAnnotationsUtils.getContent ne gère pas les références vers les examples
            io.swagger.v3.oas.annotations.media.Content[] content = annotation.content();
            stream(content).filter(contentAnnotation -> contentAnnotation.examples().length > 0 && apiResponse.getContent().containsKey(MediaType.ALL_VALUE) && (apiResponse.getContent().get(MediaType.ALL_VALUE).getExamples() == null || apiResponse.getContent().get(MediaType.ALL_VALUE).getExamples().size() != contentAnnotation.examples().length)).forEach(contentAnnotation -> stream(contentAnnotation.examples())
                    .filter(annotationExample -> isNotBlank(annotationExample.ref()))
                    .forEach(annotationExample -> apiResponse.getContent().get(MediaType.ALL_VALUE).addExamples(annotationExample.ref(), new Example().$ref(annotationExample.ref()))));

            if (isNotBlank(annotation.ref())) {
                apiResponse.set$ref(annotation.ref());
            }
            apiResponses.addApiResponse(annotation.responseCode(), apiResponse);
        });
        return apiResponses;
    }

    @Override
    public void customise(OpenAPI openApi) {
        openApi.components()

    }
}
