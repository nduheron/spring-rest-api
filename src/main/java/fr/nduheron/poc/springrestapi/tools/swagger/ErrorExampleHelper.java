package fr.nduheron.poc.springrestapi.tools.swagger;

import fr.nduheron.poc.springrestapi.tools.swagger.annotations.ErrorExample;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;
import springfox.documentation.service.ObjectVendorExtension;
import springfox.documentation.service.StringVendorExtension;
import springfox.documentation.service.VendorExtension;
import springfox.documentation.spring.web.plugins.Docket;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import static com.google.common.base.CaseFormat.UPPER_CAMEL;
import static com.google.common.base.CaseFormat.UPPER_UNDERSCORE;
import static org.apache.commons.lang3.ClassUtils.isAssignable;

@Component
@ConditionalOnClass(Docket.class)
public class ErrorExampleHelper {
    private static final Logger logger = LoggerFactory.getLogger(ErrorExampleHelper.class);
    private static final String LOCAL_DATE_TIME_VALUE = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    private static final String LOCAL_DATE_VALUE = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
    private static final String DATE_VALUE = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz").format(new Date());
    private static final String ZERO = "0";


    public ObjectVendorExtension buildExamples(ErrorExample[] errorsDoc) {
        ObjectVendorExtension examples = new ObjectVendorExtension("examples");
        Arrays.stream(errorsDoc).forEach(e -> {
            ObjectVendorExtension example = new ObjectVendorExtension(UPPER_UNDERSCORE.to(UPPER_CAMEL, e.code()));
            example.addProperty(new StringVendorExtension("code", e.code()));
            example.addProperty(new StringVendorExtension("message", e.message()));
            if (StringUtils.isNotBlank(e.attribute())) {
                example.addProperty(new StringVendorExtension("attribute", e.attribute()));
            }
            if (e.additionalsInformationsType() != Void.class) {
                example.addProperty(populateObject("additionalsInformations", e.additionalsInformationsType()));
            }
            examples.addProperty(example);

        });
        return examples;
    }


    /**
     * <p>
     * Instancie un objet avec de valeurs vides.
     * </p>
     *
     * @param <T>   le type d'objet à instancier
     * @param clazz le type d'objet à instancier
     * @return une instance de T
     */
    @SuppressWarnings("unchecked")
    private <T> VendorExtension populateObject(String name, Class<T> clazz) {
        if (clazz.isAssignableFrom(String.class)) {
            return new StringVendorExtension(name, StringUtils.EMPTY);
        } else if (isAssignable(clazz, Date.class)) {
            return new StringVendorExtension(name, DATE_VALUE);
        } else if (isAssignable(clazz, LocalDateTime.class)) {
            return new StringVendorExtension(name, LOCAL_DATE_TIME_VALUE);
        } else if (isAssignable(clazz, LocalDate.class)) {
            return new StringVendorExtension(name, LOCAL_DATE_VALUE);
        } else if (isAssignable(clazz, Number.class)) {
            return new StringVendorExtension(name, ZERO);
        } else if (isAssignable(clazz, Boolean.class)) {
            return new StringVendorExtension(name, Boolean.FALSE.toString());
        } else if (isAssignable(clazz, Collection.class)) {
            throw new IllegalStateException("Not yet implemented !!!");
        } else if (clazz.isEnum()) {
            List<?> enumValues = Arrays.asList(clazz.getEnumConstants());
            return new StringVendorExtension(name, enumValues.get(0).toString());
        } else {
            ObjectVendorExtension objectVendorExtension = new ObjectVendorExtension(name);
            Field[] listeFields = FieldUtils.getAllFields(clazz);
            for (Field field : listeFields) {
                if (!Modifier.isStatic(field.getModifiers()) && !Modifier.isFinal(field.getModifiers())) {
                    Class<?> type = field.getType();
                    objectVendorExtension.addProperty(populateObject(field.getName(), field.getType()));
                }
            }
            return objectVendorExtension;
        }
    }
}