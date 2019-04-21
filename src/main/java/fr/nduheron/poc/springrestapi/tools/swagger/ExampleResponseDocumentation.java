package fr.nduheron.poc.springrestapi.tools.swagger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.OperationBuilderPlugin;
import springfox.documentation.spi.service.contexts.OperationContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.common.SwaggerPluginSupport;

import java.io.IOException;
import java.util.*;

import static java.util.Arrays.stream;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toSet;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * FIX bug springfox qui ne gère pas l'annotation @{@link io.swagger.annotations.Example}.
 */
@Component
@Order(SwaggerPluginSupport.SWAGGER_PLUGIN_ORDER + 1000)
@ConditionalOnClass(Docket.class)
public class ExampleResponseDocumentation implements OperationBuilderPlugin {
    private static final Logger logger = LoggerFactory.getLogger(ExampleResponseDocumentation.class);

    private ObjectMapper objectMapper;

    public ExampleResponseDocumentation(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean supports(DocumentationType delimiter) {
        return true;
    }

    @Override
    public void apply(OperationContext context) {

        Operation operation = context.operationBuilder().build();
        context.findAnnotation(ApiResponses.class).transform(Optional::of).or(Optional.empty()).ifPresent(apiResponses -> {
            context.operationBuilder().responseMessages(stream(apiResponses.value()).filter(apiResponse -> isNotBlank(apiResponse.examples().value()[0].mediaType())).map(apiResponse -> {
                ObjectVendorExtension examples = new ObjectVendorExtension("examples");

                stream(apiResponse.examples().value()).forEach(example -> {
                    try {
                        examples.addProperty(jsonToVendorExtension(example.mediaType(), objectMapper.readTree(example.value())));
                    } catch (IOException e) {
                        logger.warn("Erreur lors de la génération de l'exemple json: {}.", example.value(), e);
                        examples.addProperty(new StringVendorExtension(example.mediaType(), example.value()));
                    }
                });
                ResponseMessage existingResponse = operation.getResponseMessages().stream().filter(responseMessage -> responseMessage.getCode() == apiResponse.code()).findFirst().get();
                operation.getResponseMessages().remove(existingResponse);
                return new ResponseMessageBuilder()
                        .code(existingResponse.getCode())
                        .message(existingResponse.getMessage())
                        .responseModel(existingResponse.getResponseModel())
                        .headersWithDescription(existingResponse.getHeaders())
                        .vendorExtensions(singletonList(examples))
                        .build();
            }).collect(toSet()));
        });


    }

    private VendorExtension jsonToVendorExtension(String name, JsonNode jsonNode) {
        if (jsonNode.isValueNode()) {
            return new StringVendorExtension(name, jsonNode.textValue());
        } else if (jsonNode.isArray()) {
            List<Map<String, Object>> items = new ArrayList<>();
            for (final JsonNode objNode : jsonNode) {
                items.add(objectMapper.convertValue(objNode, Map.class));
            }
            return new ListVendorExtension(name, items);
        } else {
            ObjectVendorExtension objectVendorExtension = new ObjectVendorExtension(name);
            Iterator<String> fieldsNamesIt = jsonNode.fieldNames();
            while (fieldsNamesIt.hasNext()) {
                String fieldName = fieldsNamesIt.next();
                objectVendorExtension.addProperty(jsonToVendorExtension(fieldName, jsonNode.get(fieldName)));
            }
            return objectVendorExtension;
        }
    }
}
