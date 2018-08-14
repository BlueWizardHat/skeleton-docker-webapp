package net.bluewizardhat.dockerwebapp.rest;

import javax.validation.ConstraintViolationException;

import org.springframework.core.task.TaskRejectedException;
import org.springframework.http.HttpStatus;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.context.request.async.DeferredResult;

import lombok.extern.slf4j.Slf4j;
import net.bluewizardhat.dockerwebapp.domain.logic.exception.NotFoundException;

/**
 * Base class for rest controllers with some default exception handlers and support for
 * async processing.
 */
@Slf4j
public abstract class BaseRestController {

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

	@ExceptionHandler(Exception.class)
	@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
	public void handleException(Exception e) {
		log.warn("Unhandled exception: " + e.getMessage(), e);
	}

	/**
	 * Converts a ListenableFuture to a DeferredResult.
	 */
	protected <T> DeferredResult<T> deferredResult(ListenableFuture<T> future) {
		final DeferredResult<T> deferredResult = new DeferredResult<>();
		future.addCallback(deferredResult::setResult, deferredResult::setErrorResult);
		return deferredResult;
	}

}
