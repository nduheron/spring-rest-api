package fr.nduheron.poc.springrestapi.tools.security.swagger;

import java.util.Set;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import com.google.common.base.Optional;
import com.google.common.collect.Sets;

import fr.nduheron.poc.springrestapi.tools.security.SecurityMatcher;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.service.ResponseMessage;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.OperationBuilderPlugin;
import springfox.documentation.spi.service.contexts.OperationContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.common.SwaggerPluginSupport;

/**
 * Gestion automatique de la documentation swagger pour les erreurs liées à la
 * sécurité.
 */
@Component
@Order(SwaggerPluginSupport.SWAGGER_PLUGIN_ORDER + 1000)
@ConditionalOnBean(Docket.class)
public class SecurityOperationDocumentation implements OperationBuilderPlugin {

	@Autowired
	private SecurityMatcher matcher;

	@Override
	public boolean supports(DocumentationType delimiter) {
		return true;
	}

	@Override
	public void apply(OperationContext context) {

		if (matcher.apply(context.requestMappingPattern())) {
			// UNAUTHORIZED
			ResponseMessage unauthorized = new ResponseMessageBuilder().code(HttpStatus.UNAUTHORIZED.value())
					.message("Utilisateur non authentifié.").build();
			Set<ResponseMessage> responsesMessage = Sets.newHashSet(unauthorized);
			// FORBIDDEN
			Set<String> roles = findRoles(context);
			if (!roles.isEmpty() || hasAuthorize(context)) {
				ResponseMessage forbidden = new ResponseMessageBuilder().code(HttpStatus.FORBIDDEN.value())
						.message("Utilisateur authentifié mais droits insuffisants.").build();
				responsesMessage.add(forbidden);
			}
			context.operationBuilder().responseMessages(responsesMessage);
		}
	}

	private Set<String> findRoles(OperationContext context) {
		Set<String> roles = Sets.newHashSet();

		Optional<RolesAllowed> findRolesAnnotationController = context.findControllerAnnotation(RolesAllowed.class);
		if (findRolesAnnotationController.isPresent() && !context.findAnnotation(PermitAll.class).isPresent()) {
			roles.addAll(Sets.newHashSet(findRolesAnnotationController.get().value()));
		}
		Optional<RolesAllowed> findRolesAnnotation = context.findAnnotation(RolesAllowed.class);
		if (findRolesAnnotation.isPresent()) {
			roles.addAll(Sets.newHashSet(findRolesAnnotation.get().value()));
		}

		return roles;

	}

	private boolean hasAuthorize(OperationContext context) {
		return context.findControllerAnnotation(PreAuthorize.class).isPresent()
				|| context.findControllerAnnotation(PostAuthorize.class).isPresent()
				|| context.findAnnotation(PreAuthorize.class).isPresent()
				|| context.findAnnotation(PostAuthorize.class).isPresent();
	}

}
