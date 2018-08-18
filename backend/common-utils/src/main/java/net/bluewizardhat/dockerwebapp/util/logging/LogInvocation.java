package net.bluewizardhat.dockerwebapp.util.logging;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to make the logging aspect log entry and exit of methods. When put on a class applies to all public methods
 * of that class. When put both a class and a method in the same class the values for the method takes priority.
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface LogInvocation {

	public static enum LogLevel {
		TRACE, DEBUG, INFO;
	}

	public static enum ExceptionLogLevel {
		WARN, ERROR;
	}

	/**
	 * Normal messages (Entering, Exiting, etc.) are logged at this level. Default is INFO.
	 */
	public LogLevel logLevel() default LogLevel.INFO;

	/**
	 * Log parameter values (parameters annotated with @Sensitive will have their values masked).
	 *
	 * Default <code>true</code> because it's very useful.
	 */
	public boolean logParameterValues() default true;

	/**
	 * Log return values.
	 *
	 * Default <code>false</code> to avoid excessive logging.
	 */
	public boolean logReturnValue() default false;

	/**
	 * Exceptions are logged at this level. Default is WARN.
	 */
	public ExceptionLogLevel exceptionLogLevel() default ExceptionLogLevel.WARN;

	/**
	 * Log stacktraces.
	 *
	 * Exceptions are re-thrown by the logging aspect so full stacktraces should be caught and logged at
	 * a higher level.
	 *
	 * Default <code>false</code> to avoid excessive logging.
	 */
	public boolean logStacktraces() default false;
}
