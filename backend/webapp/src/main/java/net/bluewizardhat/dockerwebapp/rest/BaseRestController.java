package net.bluewizardhat.dockerwebapp.rest;

import java.util.concurrent.RejectedExecutionException;

import javax.validation.ConstraintViolationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.http.HttpStatus;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.context.request.async.DeferredResult;

import net.bluewizardhat.dockerwebapp.domain.logic.exception.NotFoundException;

/**
 * Base class for rest controllers with some default exception handlers and support for
 * async processing.
 */
public abstract class BaseRestController {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@ExceptionHandler(IllegalArgumentException.class)
	@ResponseStatus(code = HttpStatus.BAD_REQUEST)
	public void handleException(IllegalArgumentException e) {
		log.warn("Unhandled IllegalArgumentException: " + e.getMessage(), e);
	}

	@ExceptionHandler(ConstraintViolationException.class)
	@ResponseStatus(code = HttpStatus.BAD_REQUEST)
	public void handleException(ConstraintViolationException e) {
		log.warn("Unhandled ConstraintViolationException: " + e.getMessage(), e);
	}

	@ExceptionHandler(NotFoundException.class)
	@ResponseStatus(code = HttpStatus.NOT_FOUND)
	public void handleException(NotFoundException e) {
		log.warn("Unhandled NotFoundException: " + e.getMessage(), e);
	}

	@ExceptionHandler(AsyncRequestTimeoutException.class)
	@ResponseStatus(code = HttpStatus.REQUEST_TIMEOUT)
	public void handleException(AsyncRequestTimeoutException e) {
		log.warn("Unhandled AsyncRequestTimeoutException: " + e.getMessage(), e);
	}

	@ExceptionHandler(TaskRejectedException.class)
	@ResponseStatus(code = HttpStatus.TOO_MANY_REQUESTS)
	public void handleException(TaskRejectedException e) {
		log.warn("Unhandled TaskRejectedException: " + e.getMessage(), e);
	}

	@ExceptionHandler(RejectedExecutionException.class)
	@ResponseStatus(code = HttpStatus.TOO_MANY_REQUESTS)
	public void handleException(RejectedExecutionException e) {
		log.warn("Unhandled RejectedExecutionException: " + e.getMessage(), e);
	}

	@ExceptionHandler(Exception.class)
	@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
	public void handleException(Exception e) {
		log.warn("Unhandled exception: " + e.getMessage(), e);
	}

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
