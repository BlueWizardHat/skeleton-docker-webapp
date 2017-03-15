package net.bluewizardhat.dockerwebapp.rest;

import static net.bluewizardhat.dockerwebapp.util.concurrent.RunnableWrappers.wrapSecurityContext;
import static net.bluewizardhat.dockerwebapp.util.concurrent.RunnableWrappers.wrapThreadContext;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.web.context.request.async.DeferredResult;


public class BaseRestController {

	private ExecutorService executorService = Executors.newCachedThreadPool();

	protected <T> DeferredResult<T> submit(final Callable<T> callable) {
		final DeferredResult<T> deferredResult = new DeferredResult<>();

		executorService.execute(wrapRunnable(() -> {
			try {
				T result = callable.call();
				deferredResult.setResult(result);
			} catch (Exception e) {
				deferredResult.setErrorResult(e);
			}
		}));

		return deferredResult;
	}

	protected DeferredResult<Void> execute(final Runnable runnable) {
		final DeferredResult<Void> deferredResult = new DeferredResult<>();

		executorService.execute(wrapRunnable(() -> {
			try {
				runnable.run();
				deferredResult.setResult(null);
			} catch (Exception e) {
				deferredResult.setErrorResult(e);
			}
		}));

		return deferredResult;
	}

	private Runnable wrapRunnable(final Runnable runnable) {
		return wrapSecurityContext(wrapThreadContext(runnable));
	}

}
