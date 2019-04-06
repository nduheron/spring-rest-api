package fr.nduheron.poc.springrestapi.tools.swagger;

import com.fasterxml.classmate.TypeResolver;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import fr.nduheron.poc.springrestapi.tools.exception.model.Error;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.info.BuildProperties;
import org.springframework.http.MediaType;
import org.springframework.util.Assert;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.schema.AlternateTypeRule;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.SecurityScheme;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.paths.RelativePathProvider;
import springfox.documentation.spring.web.plugins.Docket;

import javax.servlet.ServletContext;

/**
 * Factory Spring permettant de configurer swagger
 */
@ConditionalOnClass(Docket.class)
public class DocketFactory implements FactoryBean<Docket>, InitializingBean {

    @Autowired
    private TypeResolver typeResolver;

    @Autowired(required = false)
    private BuildProperties buildProperties;

    @Autowired
    private ServletContext servletContext;

    private String title;

    private String description;

    private String version;

    private Class<?>[] ignoredParameterTypes;

    private Predicate<String> selector;

    private SecurityContext securityContext;

    private SecurityScheme securityScheme;

    @Autowired(required = false)
    private AlternateTypeRule alternateTypeRule;

    @Override
    public Docket getObject() {
        Docket docket = new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo())
                .groupName(title)
                .useDefaultResponseMessages(false);

        docket.additionalModels(typeResolver.resolve(Error.class));

        if (ignoredParameterTypes != null) {
            docket.ignoredParameterTypes(ignoredParameterTypes);
        }

        if (securityScheme != null) {
            docket.securitySchemes(Lists.newArrayList(securityScheme));
        }
        if (securityContext != null) {
            docket.securityContexts(Lists.newArrayList(securityContext));
        }

        docket.produces(Sets.newHashSet(MediaType.APPLICATION_JSON_VALUE));
        docket.consumes(Sets.newHashSet(MediaType.APPLICATION_JSON_VALUE));

        docket.pathProvider(new RelativePathProvider(servletContext) {
            @Override
            public String getApplicationBasePath() {
                return servletContext.getContextPath();
            }
        });
        docket.forCodeGeneration(true);

        if (alternateTypeRule != null) {
            docket.alternateTypeRules(alternateTypeRule);
        }

        return docket.select().paths(selector).build();
    }

    @Override
    public Class<?> getObjectType() {
        return Docket.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    private ApiInfo apiInfo() {
        ApiInfoBuilder apiInfoBuilder = new ApiInfoBuilder();
        apiInfoBuilder.title(title);
        apiInfoBuilder.description(description);
        if (Strings.isNullOrEmpty(version) && buildProperties != null) {
            apiInfoBuilder.version(buildProperties.getVersion());
        } else {
            apiInfoBuilder.version(version);
        }
        return apiInfoBuilder.build();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(title, "title must not be null");
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setSelector(Predicate<String> selector) {
        this.selector = selector;
    }

    public void setIgnoredParameterTypes(Class<?>... ignoredParameterTypes) {
        this.ignoredParameterTypes = ignoredParameterTypes;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setSecurityContext(SecurityContext securityContext) {
        this.securityContext = securityContext;
    }

    public SecurityScheme getSecurityScheme() {
        return securityScheme;
    }

    public void setSecurityScheme(SecurityScheme securityScheme) {
        this.securityScheme = securityScheme;
    }


}
