package fr.nduheron.poc.springrestapi.tools.log;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import org.springframework.web.util.WebUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Filtre permettant de logger les requêtes et réponses de tous les appels REST.
 */
public class ApiLoggingFilter extends OncePerRequestFilter {

	private static final Logger LOG = LoggerFactory.getLogger(ApiLoggingFilter.class);
	private static final String UNKNOWN = "<unknown>";
	private static final String PATTERN_PASSWORD_REPLACER = "\"$1\":\"xxxxx\"";
	private static final String PATTERN_PASSWORD_EXTRACTOR = "(?i)\"(\\w*(?:password))\":\".+?\"";
	private static final String CLIENT_IP = "client_ip";

	private ObjectMapper mapper;

	public ApiLoggingFilter(ObjectMapper mapper) {
		this.mapper = mapper;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		MDC.put(CLIENT_IP, request.getRemoteAddr());

		ContentCachingRequestWrapper requestToUse = new ContentCachingRequestWrapper(request);
		ContentCachingResponseWrapper responseToUse = new ContentCachingResponseWrapper(response);
		long debut = System.currentTimeMillis();
		try {
			filterChain.doFilter(requestToUse, responseToUse);
		} finally {
			long fin = System.currentTimeMillis();
			if (response.getStatus() >= 500) {
				LOG.error(getMessage(requestToUse, responseToUse, fin - debut));
			} else if (response.getStatus() >= 400) {
				LOG.warn(getMessage(requestToUse, responseToUse, fin - debut));
			} else if (LOG.isInfoEnabled()) {
				LOG.info(getMessage(requestToUse, responseToUse, fin - debut));
			}
			responseToUse.copyBodyToResponse();

			MDC.clear();
		}

	}

	/**
	 * Construite le message de log.
	 *
	 * @return le message à logguer
	 */
	private String getMessage(final HttpServletRequest request, final HttpServletResponse response, long time) {
		HttpModel httpModel = new HttpModel();

		httpModel.setMethod(request.getMethod());
		httpModel.setPath(request.getRequestURI());
		httpModel.setStatusCode(response.getStatus());
		httpModel.setDurationInMs(time);

		if (LOG.isTraceEnabled()) {
			httpModel.setRequestHeaders(getRequestHeaders(request));
			httpModel.setResponseHeaders(getResponseHeaders(response));
		}

		if (LOG.isDebugEnabled() || response.getStatus() >= 400) {
			httpModel.setRequestContent(getRequestContent(request));
		}

		if (LOG.isDebugEnabled()) {
			httpModel.setResponseContent(getResponseContent(response));
		}

		try {
			return mapper.writeValueAsString(httpModel);
		} catch (JsonProcessingException e) {
			LOG.error("Impossible de construire le message de log", e);
		}
		return null;
	}

	private String getRequestContent(final HttpServletRequest request) {
		ContentCachingRequestWrapper wrapper = WebUtils.getNativeRequest(request, ContentCachingRequestWrapper.class);
		if (wrapper != null) {
			byte[] buf = wrapper.getContentAsByteArray();
			if (buf.length > 0) {
				try {
					String str = new String(buf, 0, buf.length, wrapper.getCharacterEncoding());
					str = str.replaceAll(PATTERN_PASSWORD_EXTRACTOR, PATTERN_PASSWORD_REPLACER);
					return str;
				} catch (UnsupportedEncodingException ex) {
					return UNKNOWN;
				}
			}
		}
		return null;
	}

	private String getResponseContent(final HttpServletResponse response) {
		ContentCachingResponseWrapper responseWrapper = WebUtils.getNativeResponse(response,
				ContentCachingResponseWrapper.class);
		if (responseWrapper != null) {
			byte[] buf = responseWrapper.getContentAsByteArray();
			if (buf.length > 0) {
				try {
					return new String(buf, 0, buf.length, responseWrapper.getCharacterEncoding());
				} catch (UnsupportedEncodingException ex) {
					return UNKNOWN;
				}
			}
		}
		return null;
	}

	private Map<String, String> getRequestHeaders(final HttpServletRequest request) {
		Enumeration<String> headerNames = request.getHeaderNames();
		Map<String, String> requestHeaders = new HashMap<>();
		while (headerNames.hasMoreElements()) {
			String key = headerNames.nextElement();
			requestHeaders.put(key, request.getHeader(key));
		}
		return requestHeaders;
	}

	private Map<String, String> getResponseHeaders(final HttpServletResponse response) {
		Map<String, String> responseHeaders = new HashMap<>();
		for (String key : response.getHeaderNames()) {
			responseHeaders.put(key, response.getHeader(key));
		}
		return responseHeaders;
	}

}
