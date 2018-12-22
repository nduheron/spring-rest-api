package fr.nduheron.poc.springrestapi.tools;

import com.google.common.base.Predicate;
import org.springframework.util.AntPathMatcher;

public class AntPathPredicate implements Predicate<String> {

    private final String antPattern;
    private AntPathMatcher matcher = new AntPathMatcher();

    public AntPathPredicate(String antPattern) {
        this.antPattern = antPattern;
    }

    @Override
    public boolean apply(String input) {
        return matcher.match(antPattern, input);
    }
}