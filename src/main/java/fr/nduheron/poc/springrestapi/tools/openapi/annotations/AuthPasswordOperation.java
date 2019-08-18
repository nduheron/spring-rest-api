package fr.nduheron.poc.springrestapi.tools.openapi.annotations;


import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Operation(security = @SecurityRequirement(name = "oauthPasswordFlow"))
public @interface AuthPasswordOperation {

    @AliasFor(
            annotation = Operation.class
    )
    String method() default "";

    @AliasFor(
            annotation = Operation.class
    )
    String[] tags() default {};

    @AliasFor(
            annotation = Operation.class
    )
    String summary() default "";

    @AliasFor(
            annotation = Operation.class
    )
    String description() default "";

    @AliasFor(
            annotation = Operation.class
    )
    RequestBody requestBody() default @RequestBody();

    @AliasFor(
            annotation = Operation.class
    )
    ExternalDocumentation externalDocs() default @ExternalDocumentation();

    @AliasFor(
            annotation = Operation.class
    )
    String operationId() default "";

    @AliasFor(
            annotation = Operation.class
    )
    Parameter[] parameters() default {};

    @AliasFor(
            annotation = Operation.class
    )
    ApiResponse[] responses() default {};

    @AliasFor(
            annotation = Operation.class
    )
    boolean deprecated() default false;

    @AliasFor(
            annotation = Operation.class
    )
    Server[] servers() default {};

    @AliasFor(
            annotation = Operation.class
    )
    Extension[] extensions() default {};


    @AliasFor(
            annotation = Operation.class
    )
    boolean ignoreJsonView() default false;
}
