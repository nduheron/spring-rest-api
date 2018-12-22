package fr.nduheron.poc.springrestapi.tools.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@ConfigurationProperties("security.config")
public class SecurityConfigProperties {

    /**
     * Liste des urls à protéger par authentification
     */
    private List<Matcher> includes = new ArrayList<>();
    /**
     * Liste des urls à exclure de l'authentification
     */
    private List<Matcher> excludes = new ArrayList<>();

    public List<Matcher> getIncludes() {
        return includes;
    }

    public void setIncludes(List<Matcher> includes) {
        this.includes = includes;
    }

    public List<Matcher> getExcludes() {
        return excludes;
    }

    public void setExcludes(List<Matcher> excludes) {
        this.excludes = excludes;
    }

    public static class Matcher {
        /**
         * @see http://etutorials.org/Programming/Java+extreme+programming/Chapter+3.+Ant/3.10+Including+and+Excluding+Files/
         */
        private String antPattern;

        public String getAntPattern() {
            return antPattern;
        }

        public void setAntPattern(String antPattern) {
            this.antPattern = antPattern;
        }
    }
}
