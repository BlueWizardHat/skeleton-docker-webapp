package net.bluewizardhat.dockerwebapp.util.logging;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.SettableListenableFuture;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.context.request.async.DeferredResult;

/**
 * @see LogInvocation
 * @see Sensitive
 */
@Order(2)
@Aspect
@Component
public class LoggingAspect {

	@LogInvocation
	private static class LogInvocationDefaults {
	}

	@Value("${net.bluewizardhat.logging.aspect.msgPrefix:true}")
	private boolean msgPrefix;

	@Pointcut("execution(public * (@org.springframework.web.bind.annotation.RestController *).*(..))")
	void methodOfRestController() {}

	@Pointcut("execution(public * (@net.bluewizardhat.dockerwebapp.util.logging.LogInvocation *).*(..))")
	void methodOfAnnotatedClass() {}

	@Pointcut("execution(@net.bluewizardhat.dockerwebapp.util.logging.LogInvocation * *.*(..))")
	void annotatedMethod() {}

	@Around("methodOfAnnotatedClass() || annotatedMethod() || methodOfRestController()")
	public Object logInvocation(ProceedingJoinPoint jp) throws Throwable {
		long startTime = System.currentTimeMillis();
		Object[] args = jp.getArgs();
		MethodSignature methodSignature = (MethodSignature) jp.getSignature();
		Method method = methodSignature.getMethod();
		Logger logger = LoggerFactory.getLogger(method.getDeclaringClass());

		LogInvocation annotation = method.getAnnotation(LogInvocation.class);
		if (annotation == null) {
			annotation = method.getDeclaringClass().getAnnotation(LogInvocation.class);
		}
		if (annotation == null) {
			annotation = LogInvocationDefaults.class.getAnnotation(LogInvocation.class);
		}

		try {
			if (args.length == 0) {
				log(logger, annotation, "Entering {}()", method.getName());
			} else if (annotation.logParameterValues()) {
				log(logger, annotation, "Entering {}({})", () -> new Object[] { method.getName(), getLoggableArgs(methodSignature, method, args) });
			} else {
				log(logger, annotation, "Entering {}({})", () -> new Object[] { method.getName(), getParamNamesOrTypes(methodSignature, method, args) });
			}

			Object result = jp.proceed();

			if (result instanceof DeferredResult) {
				log(logger, annotation, "Exiting {} async with DeferredResult - processing may continue in another thread", method.getName());
				return handleDeferredResult((DeferredResult<?>) result, logger, method, annotation, startTime);
			} else if (result instanceof ListenableFuture) {
				if (method.getAnnotation(Async.class) == null) {
					// If using Springs @Async chances are the method itself is not async so will not return ahead of time
					log(logger, annotation, "Exiting {} async with Spring ListenableFuture - processing may continue in another thread", method.getName());
				}
				return handleSpringListenableFuture((ListenableFuture<?>) result, logger, method, annotation, startTime);
			} else if (result instanceof Future) {
				// Can't log actual return of generic Future
				log(logger, annotation, "Exiting {} async with Future - processing may continue in another thread"
								+ " - Unable to hook, cannot log actual exit!", method.getName());
			} else {
				logNormalReturn(logger, method, annotation, startTime, result);
			}

			return  result;
		} catch (Throwable thr) {
			logErrorReturn(logger, method, annotation, startTime, thr);
			throw thr;
		}
	}

	private String[] getParamNamesOrTypes(MethodSignature methodSignature, Method method, Object[] args) {
		String[] paramNames = methodSignature.getParameterNames();
		if (paramNames == null) {
			Class<?>[] paramTypes = method.getParameterTypes();
			paramNames = new String[paramTypes.length];
			for (int i = 0; i < paramTypes.length; i++) {
				paramNames[i] = paramTypes[i].getSimpleName();
			}
		}
		return paramNames;
	}

	private String[] getLoggableArgs(MethodSignature methodSignature, Method method, Object[] args) {
		String[] loggable = new String[args.length];
		Parameter[] parameters = method.getParameters();
		String[] paramNames = methodSignature.getParameterNames();

		for (int i = 0; i < parameters.length; i++) {
			Sensitive sensitive = parameters[i].getAnnotation(Sensitive.class);
			loggable[i] = param2String(i, paramNames, args, sensitive);
		}
		return loggable;
	}

	/**
	 * Playing it safe.
	 */
	private String param2String(int index, String[] paramNames, Object[] args, Sensitive sensitive) {
		if (paramNames != null && paramNames.length > index) {
			return paramNames[index] + "=" + valueToString(args[index], sensitive);
		}
		return valueToString(args[index], sensitive);
	}

	private String valueToString(Object value, Sensitive sensitive) {
		if (value == null) {
			return "<null>";
		} else if (sensitive != null) {
			return "<hidden>";
		} else {
			return "'" + value.toString() + "'";
		}
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	private DeferredResult<?> handleDeferredResult(DeferredResult<?> result, Logger logger, Method method, LogInvocation annotation, long startTime) {
		Long timeout = getDeferredResultField(logger, result, "timeout", Long.class);
		Object timeoutResult = getDeferredResultField(logger, result, "timeoutResult", Object.class);

		final DeferredResult loggingResult = (timeoutResult != null)
				? new DeferredResult<>(timeout, timeoutResult)
				: new DeferredResult<>(timeout);

		result.setResultHandler(actualResult -> {
			if (actualResult instanceof Throwable) {
				logErrorReturn(logger, method, annotation, startTime, ((Throwable) actualResult));
			} else {
				logNormalReturn(logger, method, annotation, startTime, actualResult);
			}
			loggingResult.setResult(actualResult);
		});

		loggingResult.onTimeout(() -> {
			Runnable timeoutCallback = getDeferredResultField(logger, result, "timeoutCallback", Runnable.class);
			if (timeoutCallback != null) {
				timeoutCallback.run();
			}
			if (!result.hasResult()) {
				result.setErrorResult(new AsyncRequestTimeoutException());
			}
		});

		loggingResult.onError(thr -> {
			Consumer<Throwable> errorCallback = getDeferredResultField(logger, result, "errorCallback", Consumer.class);
			if (errorCallback != null) {
				errorCallback.accept((Throwable) thr);
			}
		});

		loggingResult.onCompletion(() -> {
			Runnable completionCallback = getDeferredResultField(logger, result, "completionCallback", Runnable.class);
			if (completionCallback != null) {
				completionCallback.run();
			}
		});

		return loggingResult;
	}

	@SuppressWarnings("unchecked")
	private <T> T getDeferredResultField(Logger logger, Object src, String name,  Class<T> targetClass) {
		try {
			Field field = DeferredResult.class.getDeclaredField(name);
			field.setAccessible(true);
			return (T) field.get(src);
		} catch (IllegalAccessException|IllegalArgumentException|NoSuchFieldException e) {
			logger.warn(prefixMessage("Unable to read DeferredResult.{} - {}: {}"), name, e.getClass().getSimpleName(), e.getMessage());
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	private ListenableFuture<?> handleSpringListenableFuture(ListenableFuture<?> result, Logger logger, Method method, LogInvocation annotation, long startTime) {
		@SuppressWarnings("rawtypes")
		final SettableListenableFuture loggingResult = new SettableListenableFuture() {
			@Override
			public boolean cancel(boolean mayInterruptIfRunning) {
				boolean cancelSuccess = result.cancel(mayInterruptIfRunning);
				if (cancelSuccess) {
					super.cancel(mayInterruptIfRunning);
					log(logger, annotation, "Cancelled {}", method.getName());
				}
				return cancelSuccess;
			}
		};

		result.addCallback(
			actualResult -> {
				logNormalReturn(logger, method, annotation, startTime, actualResult);
				loggingResult.set(actualResult);
			},
			ex -> {
				logErrorReturn(logger, method, annotation, startTime, ex);
				loggingResult.setException(ex);
			});

		return loggingResult;
	}

	private void logNormalReturn(Logger logger, Method method, LogInvocation annotation, long startTime, Object returnValue) {
		long time = System.currentTimeMillis() - startTime;
		if (annotation.logReturnValue()) {
			log(logger, annotation, "Exiting {} (after {} ms) with return value '{}'", method.getName(), time, returnValue);
		} else {
			log(logger, annotation, "Exiting {} (after {} ms) without errors", method.getName(), time);
		}
	}

	/**
	 * Log method that avoid calling expensive operations if the log level is not active.
	 */
	private void log(Logger logger, LogInvocation annotation, String msg, Supplier<Object[]> args) {
		switch (annotation.logLevel()) {
		case INFO:
			if (logger.isInfoEnabled()) {
				logger.info(prefixMessage(msg), args.get());
			}
			break;
		case DEBUG:
			if (logger.isDebugEnabled()) {
				logger.debug(prefixMessage(msg), args.get());
			}
			break;
		case TRACE:
			if (logger.isTraceEnabled()) {
				logger.trace(prefixMessage(msg), args.get());
			}
			break;
		}
	}

	private void log(Logger logger, LogInvocation annotation, String msg, Object... args) {
		switch (annotation.logLevel()) {
		case INFO:
			logger.info(prefixMessage(msg), args);
			break;
		case DEBUG:
			logger.debug(prefixMessage(msg), args);
			break;
		case TRACE:
			logger.trace(prefixMessage(msg), args);
			break;
		}
	}

	private void logErrorReturn(Logger logger, Method method, LogInvocation annotation, long startTime, Throwable thr) {
		long time = System.currentTimeMillis() - startTime;

		StringBuilder chain = new StringBuilder(thr.getClass().getSimpleName());
		String thrmsg = describeThrowable(thr, chain);
		String msg = prefixMessage("Exiting {} (after {} ms) with {}: '{}'");
		Object[] args = annotation.logStacktraces()
				? new Object[] { method.getName(), time, chain, thrmsg, thr }
				: new Object[] { method.getName(), time, chain, thrmsg };

		switch (annotation.exceptionLogLevel()) {
		case ERROR:
			logger.error(msg, args);
			break;
		case WARN:
			logger.warn(msg, args);
			break;
		}
	}

	private String describeThrowable(Throwable original, StringBuilder chain) {
		Throwable lastCause = original;
		Throwable nextCause = original.getCause();
		int depth = 1;
		while (nextCause != null) {
			depth++;
			lastCause = nextCause;
			nextCause = nextCause.getCause();
		}
		if (depth == 2) {
			chain.append(" < ").append(lastCause.getClass().getSimpleName());
		} else if (depth > 2) {
			chain.append(" < ").append(depth - 2).append(" more < ").append(lastCause.getClass().getSimpleName());
		}
		return lastCause.getMessage();
	}

	private String prefixMessage(String msg) {
		return msgPrefix ? "«LoggingAspect» " + msg : msg;
	}
}
