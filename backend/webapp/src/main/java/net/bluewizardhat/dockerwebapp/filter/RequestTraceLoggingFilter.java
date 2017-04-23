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

import org.apache.logging.log4j.ThreadContext;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Tags all requests with a traceId used for logging.
 */
@Slf4j
@Component
public class RequestTraceLoggingFilter implements Filter {

	private static final String contextKey = "logTraceId";

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		log.debug("initialized");
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		String traceId = UUID.randomUUID().toString();
		ThreadContext.put(contextKey, traceId);
		request.setAttribute(contextKey, traceId);
		long start = System.currentTimeMillis();

		String method = "";
		String path = "";

		if (request instanceof HttpServletRequest) {
			HttpServletRequest httpRequest = ((HttpServletRequest) request);
			method = httpRequest.getMethod();
			path = httpRequest.getRequestURI();
			log.debug("{} '{}' - begin", method, path);
		}

		chain.doFilter(request, response);

		if (request instanceof HttpServletRequest) {
			long time = System.currentTimeMillis() - start;
			log.debug("{} '{}' - end ({} ms)", method, path, time);
		}

		ThreadContext.remove(contextKey);
	}

	@Override
	public void destroy() {
		log.debug("destroyed");
	}

}
