package net.bluewizardhat.dockerwebapp.rest;

import java.util.concurrent.Callable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.async.DeferredResult;

import lombok.extern.slf4j.Slf4j;
import net.bluewizardhat.dockerwebapp.util.concurrent.WebExecutorService;

@Slf4j
public abstract class BaseRestController {

	@Autowired
	private WebExecutorService executorService;

	@ExceptionHandler(IllegalArgumentException.class)
	@ResponseStatus(code = HttpStatus.BAD_REQUEST)
	public void handleException(IllegalArgumentException e) {
		log.warn("Unhandled exception: " + e.getMessage(), e);
	}

	@ExceptionHandler(Exception.class)
	@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
	public void handleException(Exception e) {
		log.warn("Unhandled exception: " + e.getMessage(), e);
	}

	/**
	 * Executes the given Callable in the background and returns a DeferredResult for the result.
	 */
	protected <T> DeferredResult<T> submit(final Callable<T> callable) {
		final DeferredResult<T> deferredResult = new DeferredResult<>();

		executorService.executeWrapped(() -> {
			try {
				T result = callable.call();
				deferredResult.setResult(result);
			} catch (Exception e) {
				deferredResult.setErrorResult(e);
			}
		});

		return deferredResult;
	}

	/**
	 * Executes the given Runnable in the background and returns a DeferredResult for the result.
	 */
	protected DeferredResult<Void> execute(final Runnable runnable) {
		final DeferredResult<Void> deferredResult = new DeferredResult<>();

		executorService.executeWrapped(() -> {
			try {
				runnable.run();
				deferredResult.setResult(null);
			} catch (Exception e) {
				deferredResult.setErrorResult(e);
			}
		});

		return deferredResult;
	}

}
