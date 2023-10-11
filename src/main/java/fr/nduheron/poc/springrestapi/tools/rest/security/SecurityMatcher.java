package fr.nduheron.poc.springrestapi.tools.rest.security;

import fr.nduheron.poc.springrestapi.tools.common.AntPathPredicate;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.function.Predicate;

/**
 * Matcher permettant de valider si une url est soumise à l'authentification ou
 * non. Si aucune configuration de spécifiée, on protège toutes les urls.
 */
public class SecurityMatcher implements RequestMatcher, Predicate<String> {

    private final Predicate<String> pathMatcher;

    public SecurityMatcher(SecurityConfigProperties securityProperties) {
        Predicate<String> includePathMatcher;
        if (CollectionUtils.isEmpty(securityProperties.getIncludes())) {
            // si aucune configuration de spécifiée, on protège toutes les urls
            includePathMatcher = new AntPathPredicate("/**");
        } else {
            includePathMatcher = securityProperties.getIncludes().stream()
                    .map(matcher -> (Predicate<String>) new AntPathPredicate(matcher.getAntPattern()))
                    .reduce(Predicate::or)
                    .orElse(t -> false);
        }

        if (CollectionUtils.isEmpty(securityProperties.getExcludes())) {
            pathMatcher = includePathMatcher;
        } else {
            pathMatcher = includePathMatcher.and(
                    securityProperties.getExcludes().stream()
                            .map(matcher -> (Predicate<String>) new AntPathPredicate(matcher.getAntPattern()))
                            .reduce(Predicate::or)
                            .orElse(t -> false)
                            .negate()
            );
        }
    }

    @Override
    public boolean matches(HttpServletRequest request) {
        return test(getRequestPath(request));
    }

    @Override
    public boolean test(String input) {
        return pathMatcher.test(input);
    }

    private String getRequestPath(HttpServletRequest request) {
        String url = request.getServletPath();

        if (request.getPathInfo() != null) {
            url += request.getPathInfo();
        }

        return url;
    }
}
