package net.bluewizardhat.dockerwebapp.util;

import java.time.OffsetDateTime;
import java.util.concurrent.RejectedExecutionException;

import javax.validation.ConstraintViolationException;

import org.springframework.core.task.TaskRejectedException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;

import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import net.bluewizardhat.dockerwebapp.domain.logic.exception.NotFoundException;

@Slf4j
@RestControllerAdvice
public class CustomResponseEntityExceptionHandler {

	@Value
	public static class ErrorResponse {
		private final int httpStatus;
		private final String reasonPhrase;
		private final String exception;
		private final String message;
		private final OffsetDateTime timestamp = OffsetDateTime.now();

		public static ErrorResponse from(HttpStatus httpStatus, Exception e) {
			return new ErrorResponse(
					httpStatus.value(), httpStatus.getReasonPhrase(),
					e.getClass().getName(), e.getMessage());
		}
	}

	@ExceptionHandler(IllegalArgumentException.class)
	@ResponseStatus(code = HttpStatus.BAD_REQUEST)
	@ResponseBody
	public ErrorResponse handleException(IllegalArgumentException e) {
		log.warn("Unhandled IllegalArgumentException: '{}'", e.getMessage());
		return ErrorResponse.from(HttpStatus.BAD_REQUEST, e);
	}

	@ExceptionHandler(ConstraintViolationException.class)
	@ResponseStatus(code = HttpStatus.BAD_REQUEST)
	@ResponseBody
	public ErrorResponse handleException(ConstraintViolationException e) {
		log.warn("Unhandled ConstraintViolationException: '{}'", e.getMessage());
		return ErrorResponse.from(HttpStatus.BAD_REQUEST, e);
	}

	@ExceptionHandler(NotFoundException.class)
	@ResponseStatus(code = HttpStatus.NOT_FOUND)
	@ResponseBody
	public ErrorResponse handleException(NotFoundException e) {
		log.warn("Unhandled NotFoundException: '{}'", e.getMessage());
		return ErrorResponse.from(HttpStatus.NOT_FOUND, e);
	}

	@ExceptionHandler(AsyncRequestTimeoutException.class)
	@ResponseStatus(code = HttpStatus.REQUEST_TIMEOUT)
	@ResponseBody
	public ErrorResponse handleException(AsyncRequestTimeoutException e) {
		log.error("Unhandled AsyncRequestTimeoutException");
		return ErrorResponse.from(HttpStatus.REQUEST_TIMEOUT, e);
	}

	@ExceptionHandler(TaskRejectedException.class)
	@ResponseStatus(code = HttpStatus.TOO_MANY_REQUESTS)
	@ResponseBody
	public ErrorResponse handleException(TaskRejectedException e) {
		log.error("Unhandled TaskRejectedException: '{}'", e.getMessage(), e);
		return ErrorResponse.from(HttpStatus.TOO_MANY_REQUESTS, e);
	}

	@ExceptionHandler(RejectedExecutionException.class)
	@ResponseStatus(code = HttpStatus.TOO_MANY_REQUESTS)
	@ResponseBody
	public ErrorResponse handleException(RejectedExecutionException e) {
		log.error("Unhandled RejectedExecutionException: '{}'", e.getMessage(), e);
		return ErrorResponse.from(HttpStatus.TOO_MANY_REQUESTS, e);
	}

	@ExceptionHandler(Exception.class)
	@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
	@ResponseBody
	public ErrorResponse handleException(Exception e) {
		log.error("Unhandled {}: '{}'", e.getClass().getSimpleName(), e.getMessage(), e);
		return ErrorResponse.from(HttpStatus.INTERNAL_SERVER_ERROR, e);
	}

}
