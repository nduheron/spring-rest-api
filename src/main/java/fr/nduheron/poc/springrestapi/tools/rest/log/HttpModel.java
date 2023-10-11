package fr.nduheron.poc.springrestapi.tools.rest.log;

import java.util.Map;

/**
 * Représente une requête HTTP.
 */
public class HttpModel {

    public Request request = new Request();
    public Response response = new Response();

    class Request {
        public String method;
        public String url;
        public String query;
        public Map<String, String> headers;
        public String content;
    }

    class Response {
        public int statusCode;
        public String statusFamily;
        public long duration;
        public Map<String, String> headers;
        public String content;
    }
}
