package fr.nduheron.poc.springrestapi.tools;

import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
@Scope("cucumber-glue")
public class Holder {

    private final HttpHeaders headers;

    private HttpStatus statusCode;
    private String body;
    private int version;
    private String etag;

    public Holder() {
        headers = new HttpHeaders();
        headers.add(HttpHeaders.ACCEPT_LANGUAGE, Locale.ENGLISH.getLanguage());
    }

    public HttpHeaders getHeaders() {
        if (etag != null) {
            headers.add(HttpHeaders.IF_NONE_MATCH, etag);
        }
        return headers;
    }

    public HttpStatus getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(HttpStatus statusCode) {
        this.statusCode = statusCode;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getEtag() {
        return etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }
}
