package fr.nduheron.poc.springrestapi.tools.swagger;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import com.fasterxml.classmate.TypeResolver;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import fr.nduheron.poc.springrestapi.tools.exception.model.ErrorParameter;
import fr.nduheron.poc.springrestapi.tools.exception.model.FunctionalError;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.SecurityScheme;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * Factory Spring permettant de configurer swagger
 *
 */
public class DocketFactory implements FactoryBean<Docket>, InitializingBean {

	@Autowired
	private TypeResolver typeResolver;

	private String title;

	private String description;

	private String version;

	private String groupName;

	private Class<?>[] ignoredParameterTypes;

	private Predicate<String> selector;

	private SecurityContext securityContext;

	private SecurityScheme securityScheme;

	@Override
	public Docket getObject() throws Exception {
		Docket docket = new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo())
				.groupName(Strings.isNullOrEmpty(groupName) ? title : groupName).useDefaultResponseMessages(false);

		docket.additionalModels(typeResolver.resolve(ErrorParameter.class),
				typeResolver.resolve(FunctionalError.class));

		if (ignoredParameterTypes != null) {
			docket.ignoredParameterTypes(ignoredParameterTypes);
		}

		if (securityScheme != null) {
			docket.securitySchemes(Lists.newArrayList(securityScheme));
		}
		if (securityContext != null) {
			docket.securityContexts(Lists.newArrayList(securityContext));
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
		if (!Strings.isNullOrEmpty(title)) {
			apiInfoBuilder.title(title);
		}
		if (!Strings.isNullOrEmpty(description)) {
			apiInfoBuilder.description(description);
		}
		apiInfoBuilder.version(version);
		return apiInfoBuilder.build();
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(title, "title must not be null");
		Assert.notNull(selector, "selector must not be null");
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
