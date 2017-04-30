package net.bluewizardhat.dockerwebapp.filter;

import java.io.IOException;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Logs requests and also tags all requests with a traceId that can be used for logging.
 */
@Slf4j
@Order(2)
@Component
public class RequestLoggingFilter implements Filter {

	private static final String mdcConversationIdKey = "conversationId";
	private static final String conversationIdCookieName = "conversationId";

	private static final String mdcTraceIdKey = "requestTraceId";
	private static final String attributeTraceIdKey = "RequestLoggingFilter.requestTraceId";
	private static final String attributeStartTimeKey = "RequestLoggingFilter.requestStartTimeMillis";

	private static final String[] ignoredPaths = { "///webjars/", "///swagger", "///v2/api-docs" };

	private static final Predicate<String> uuidPattern = Pattern.compile(
			"^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$").asPredicate();

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		log.debug("Initialized");
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
		if (!(request instanceof HttpServletRequest) || !(response instanceof HttpServletResponse)) {
			throw new ServletException("RequestLoggingFilter only supports HTTP requests");
		}
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		doFilterInternal(httpRequest, httpResponse, filterChain);
	}

	@Override
	public void destroy() {
		log.debug("Destroyed");
	}

	private void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		try {
			String method = request.getMethod();
			String path = request.getRequestURI();

			for (String ignoredPath : ignoredPaths) {
				if (path.startsWith(ignoredPath)) {
					filterChain.doFilter(request, response);
					return;
				}
			}

			MDC.put(mdcConversationIdKey, getConversationId(request, response));

			String traceId = (String) request.getAttribute(attributeTraceIdKey);
			if (traceId == null) {
				// First invocation
				traceId = UUID.randomUUID().toString();
				long start = System.currentTimeMillis();

				MDC.put(mdcTraceIdKey, traceId);
				request.setAttribute(attributeTraceIdKey, traceId);
				request.setAttribute(attributeStartTimeKey, start);

				log.info("{} '{}' - begin", method, path);

				filterChain.doFilter(request, response);

				long time = System.currentTimeMillis() - start;
				log.info("{} '{}' - end ({} ms)", method, path, time);
			} else {
				// Second invocation in case of DeferredResult
				MDC.put(mdcTraceIdKey, traceId);
				filterChain.doFilter(request, response);
				long start = (Long) request.getAttribute(attributeStartTimeKey);
				long time = System.currentTimeMillis() - start;
				log.info("{} '{}' - async end ({} ms)", method, path, time);
			}
		} finally {
			MDC.remove(mdcTraceIdKey);
			MDC.remove(mdcConversationIdKey);
		}
	}

	private String getConversationId(HttpServletRequest request, HttpServletResponse response) {
		Cookie[] cookies = request.getCookies();
		String conversationId = null;
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (conversationIdCookieName.equals(cookie.getName())) {
					conversationId = cookie.getValue();
					break;
				}
			}
		}

		if (conversationId == null || !uuidPattern.test(conversationId)) {
			conversationId = UUID.randomUUID().toString();
			Cookie cookie = new Cookie(conversationIdCookieName, conversationId);
			cookie.setHttpOnly(true);
			cookie.setPath("/");
			response.addCookie(cookie);
		}

		return conversationId;
	}

}
