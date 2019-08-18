package fr.nduheron.poc.springrestapi.tools.openapi.annotations;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ApiResponse(responseCode = "400", content = @Content(array = @ArraySchema(schema = @Schema(ref = "Error")), examples = @ExampleObject(ref = "InvalidFormat")))
public @interface ApiDefaultResponse400 {

}
