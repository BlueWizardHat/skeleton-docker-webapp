package net.bluewizardhat.dockerwebapp.util.concurrent;

import java.util.Map;

import org.apache.logging.log4j.ThreadContext;
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
		return wrapSecurityContext(wrapThreadContext(runnable));
	}

	/**
	 * Wraps a Runnable so that the executing thread will retain the spring security context of the
	 * originating thread.
	 */
	public static Runnable wrapSecurityContext(final Runnable runnable) {
		return DelegatingSecurityContextRunnable.create(runnable, SecurityContextHolder.getContext());
	}

	/**
	 * Wraps a Runnable so that the executing thread will retain the log4j2 thread context of the
	 * originating thread.
	 */
	public static Runnable wrapThreadContext(final Runnable runnable) {
		final Map<String, String> context = ThreadContext.getContext();

		return () -> {
			final Map<String, String> original = ThreadContext.getContext();
			try {
				overwriteThreadContext(context);
				runnable.run();
			} finally {
				overwriteThreadContext(original);
			}
		};
	}

	private static void overwriteThreadContext(final Map<String, String> context) {
		ThreadContext.clearMap();
		if (context != null) {
			ThreadContext.putAll(context);
		}
	}

}
