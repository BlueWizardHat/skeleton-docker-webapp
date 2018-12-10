package net.bluewizardhat.dockerwebapp.rest;

import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.context.request.async.DeferredResult;

import lombok.extern.slf4j.Slf4j;

/**
 * Base class for rest controllers with helper methods for for async processing.
 */
@Slf4j
public abstract class BaseRestController {

	/**
	 * Converts a ListenableFuture to a DeferredResult with a custom timeout and a timeout result for long running operations.
	 */
	protected <T> DeferredResult<T> deferredResult(ListenableFuture<T> future, long requestTimeOutMillis, T timeoutResult) {
		return deferredResult(new DeferredResult<>(requestTimeOutMillis, timeoutResult), future);
	}

	/**
	 * Converts a ListenableFuture to a DeferredResult with a custom timeout for long running operations.
	 */
	protected <T> DeferredResult<T> deferredResult(ListenableFuture<T> future, long requestTimeOutMillis) {
		return deferredResult(new DeferredResult<>(requestTimeOutMillis), future);
	}

	/**
	 * Converts a ListenableFuture to a DeferredResult with default timeout (30 seconds in tomcat).
	 */
	protected <T> DeferredResult<T> deferredResult(ListenableFuture<T> future) {
		return deferredResult(new DeferredResult<>(), future);
	}

	private <T> DeferredResult<T> deferredResult(DeferredResult<T> deferredResult, ListenableFuture<T> future) {
		long startTime = System.currentTimeMillis();
		deferredResult.onTimeout(() -> log.warn("Timeout after {} ms", System.currentTimeMillis() - startTime));
		future.addCallback(deferredResult::setResult, deferredResult::setErrorResult);
		return deferredResult;
	}

}
