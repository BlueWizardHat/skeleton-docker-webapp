package net.bluewizardhat.dockerwebapp.filter;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.ThreadContext;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Logs requests and also tags all requests with a traceId that can be used for logging.
 */
@Slf4j
@Component
public class RequestLoggingFilter implements Filter {

	private static final String threadContextTraceIdKey = "loggingTraceId";
	private static final String attributeTraceIdKey = "RequestLoggingFilter.loggingTraceId";
	private static final String attributeStartTimeKey = "RequestLoggingFilter.requestStartTimeMillis";
	private static final String[] ignoredPaths = { "///webjars/", "///swagger", "///v2/api-docs" };

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		log.debug("Initialized");
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
		if (!(request instanceof HttpServletRequest) || !(response instanceof HttpServletResponse)) {
			throw new ServletException("RequestTraceLoggingFilter only supports HTTP requests");
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

			String traceId = (String) request.getAttribute(attributeTraceIdKey);
			if (traceId == null) {
				// First invocation
				traceId = UUID.randomUUID().toString();
				long start = System.currentTimeMillis();

				ThreadContext.put(threadContextTraceIdKey, traceId);
				request.setAttribute(attributeTraceIdKey, traceId);
				request.setAttribute(attributeStartTimeKey, start);

				log.info("{} '{}' - begin", method, path);

				filterChain.doFilter(request, response);

				long time = System.currentTimeMillis() - start;
				log.info("{} '{}' - end ({} ms)", method, path, time);
			} else {
				// Second invocation in case of DeferredResult
				ThreadContext.put(threadContextTraceIdKey, traceId);
				filterChain.doFilter(request, response);
				long start = (Long) request.getAttribute(attributeStartTimeKey);
				long time = System.currentTimeMillis() - start;
				log.info("{} '{}' - async end ({} ms)", method, path, time);
			}
		} finally {
			ThreadContext.remove(threadContextTraceIdKey);
		}
	}

}
