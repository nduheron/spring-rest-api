package fr.nduheron.poc.springrestapi.tools.log;

import java.util.Arrays;
import java.util.List;

public class LogProperties {

    private String path;
    private List<String> excludePaths;
    private List<String> obfuscateParams;
    private List<String> obfuscateHeader;
    private boolean bodyEnabled = true;

    public List<String> getExcludePaths() {
        return excludePaths;
    }

    public void setExcludePaths(String[] excludePaths) {
        this.excludePaths = Arrays.asList(excludePaths);
    }

    public List<String> getObfuscateParams() {
        return obfuscateParams;
    }

    public void setObfuscateParams(String[] obfuscateParams) {
        this.obfuscateParams = Arrays.asList(obfuscateParams);
    }

    public List<String> getObfuscateHeader() {
        return obfuscateHeader;
    }

    public void setObfuscateHeader(String[] obfuscateHeader) {
        this.obfuscateHeader = Arrays.asList(obfuscateHeader);
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isBodyEnabled() {
        return bodyEnabled;
    }

    public void setBodyEnabled(boolean bodyEnabled) {
        this.bodyEnabled = bodyEnabled;
    }
}
