package net.bluewizardhat.dockerwebapp.util.concurrent;

import java.util.Map;

import org.slf4j.MDC;
import org.springframework.security.concurrent.DelegatingSecurityContextRunnable;
import org.springframework.security.core.context.SecurityContextHolder;

import lombok.experimental.UtilityClass;

/**
 * Utility class for wrapping Runnables.
 */
@UtilityClass
public class RunnableWrappers {

	/**
	 * Wraps a Runnable with both wrapSecurityContext and wrapThreadContext.
	 */
	public static Runnable wrapRunnable(final Runnable runnable) {
		return wrapSecurityContext(wrapLoggingContext(runnable));
	}

	/**
	 * Wraps a Runnable so that the executing thread will retain the spring security context of the
	 * originating thread.
	 */
	public static Runnable wrapSecurityContext(final Runnable runnable) {
		return DelegatingSecurityContextRunnable.create(runnable, SecurityContextHolder.getContext());
	}

	/**
	 * Wraps a Runnable so that the executing thread will retain the logging context of the
	 * originating thread.
	 */
	public static Runnable wrapLoggingContext(final Runnable runnable) {
		final Map<String, String> context = MDC.getCopyOfContextMap();

		return () -> {
			final Map<String, String> original = MDC.getCopyOfContextMap();
			try {
				overwriteLoggingContext(context);
				runnable.run();
			} finally {
				overwriteLoggingContext(original);
			}
		};
	}

	private static void overwriteLoggingContext(Map<String, String> context) {
		MDC.clear();
		if (context != null) {
			MDC.setContextMap(context);
		}
	}

}
