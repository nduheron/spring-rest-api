package fr.nduheron.poc.springrestapi.tools.security;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

import fr.nduheron.poc.springrestapi.tools.AntPathPredicate;
import fr.nduheron.poc.springrestapi.tools.security.SecurityConfigProperties.Matcher;

/**
 * Matcher permettant de valider si une url est soumise à l'authentification ou
 * non. Si aucune configuration de spécifiée, on protège toutes les urls.
 *
 */
@Component
public class SecurityMatcher implements RequestMatcher, Predicate<String> {

	@Autowired
	private SecurityConfigProperties securityProperties;

	private Predicate<String> pathMatcher;

	@PostConstruct
	void init() {
		Predicate<String> includePathMatcher;
		if (CollectionUtils.isEmpty(securityProperties.getIncludes())) {
			// si aucune configuration de spécifiée, on protège toutes les urls
			includePathMatcher = new AntPathPredicate("/**");
		} else {
			List<Predicate<String>> includesMatcher = new ArrayList<>(securityProperties.getIncludes().size());
			for (Matcher match : securityProperties.getIncludes()) {
				includesMatcher.add(new AntPathPredicate( match.getAntPattern()));
			}
			includePathMatcher = Predicates.or(includesMatcher);
		}

		if (CollectionUtils.isEmpty(securityProperties.getExcludes())) {
			pathMatcher = includePathMatcher;
		} else {
			List<Predicate<String>> excludesAntMatchers = new ArrayList<>();
			for (Matcher match : securityProperties.getExcludes()) {
				excludesAntMatchers.add(new AntPathPredicate(match.getAntPattern()));
			}
			pathMatcher = Predicates.and(includePathMatcher, Predicates.not(Predicates.and(excludesAntMatchers)));
		}
	}

	@Override
	public boolean matches(HttpServletRequest request) {
		return apply(getRequestPath(request));
	}

	@Override
	public boolean apply(String input) {
		return pathMatcher.apply(input);
	}

	private String getRequestPath(HttpServletRequest request) {
		String url = request.getServletPath();

		if (request.getPathInfo() != null) {
			url += request.getPathInfo();
		}

		return url;
	}
}
