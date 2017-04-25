package net.bluewizardhat.dockerwebapp.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import net.bluewizardhat.dockerwebapp.domain.logic.security.UserDetailsAdapter;

/**
 * A filter that puts the id and username of the logged in user on the MDC.
 */
@Slf4j
@Order(1)
@Component
public class UserLoggingFilter implements Filter {

	private static final String mdcUidKey = "loggingUserId";
	private static final String mdcUsernameKey = "loggingUserName";

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		log.debug("Initialized");
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
		UserDetailsAdapter.getLoggedInUser().map(u -> u.getUser())
			.ifPresent(u -> {
				MDC.put(mdcUidKey, u.getId().toString());
				MDC.put(mdcUsernameKey, u.getUserName());
			});
		try {
			filterChain.doFilter(request, response);
		} finally {
			MDC.remove(mdcUidKey);
			MDC.remove(mdcUsernameKey);
		}
	}

	@Override
	public void destroy() {
		log.debug("Destroyed");
	}

}
