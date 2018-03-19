package fr.nduheron.poc.springrestapi.tools.log;

import java.io.Serializable;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonRawValue;

/**
 * Représente une requête HTTP.
 *
 */
public class HttpModel implements Serializable {

	private static final long serialVersionUID = 1L;

	private String method;
	private String path;
	private int statusCode;
	private long durationInMs;
	private Map<String, String> requestHeaders;
	@JsonRawValue
	private String requestContent;
	private Map<String, String> responseHeaders;
	@JsonRawValue
	private String responseContent;

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public long getDurationInMs() {
		return durationInMs;
	}

	public void setDurationInMs(long durationInMs) {
		this.durationInMs = durationInMs;
	}

	public Map<String, String> getRequestHeaders() {
		return requestHeaders;
	}

	public void setRequestHeaders(Map<String, String> requestHeaders) {
		this.requestHeaders = requestHeaders;
	}

	public String getRequestContent() {
		return requestContent;
	}

	public void setRequestContent(String requestContent) {
		this.requestContent = requestContent;
	}

	public Map<String, String> getResponseHeaders() {
		return responseHeaders;
	}

	public void setResponseHeaders(Map<String, String> responseHeaders) {
		this.responseHeaders = responseHeaders;
	}

	public String getResponseContent() {
		return responseContent;
	}

	public void setResponseContent(String responseContent) {
		this.responseContent = responseContent;
	}

}
