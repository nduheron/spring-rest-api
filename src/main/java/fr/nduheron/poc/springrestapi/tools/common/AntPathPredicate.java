package fr.nduheron.poc.springrestapi.tools.common;

import org.springframework.util.AntPathMatcher;

import java.util.function.Predicate;

public class AntPathPredicate implements Predicate<String> {

    private final String antPattern;
    private AntPathMatcher matcher = new AntPathMatcher();

    public AntPathPredicate(String antPattern) {
        this.antPattern = antPattern;
    }

    @Override
    public boolean test(String input) {
        return matcher.match(antPattern, input);
    }
}