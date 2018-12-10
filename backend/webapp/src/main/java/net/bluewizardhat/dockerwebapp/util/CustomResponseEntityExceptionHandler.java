package net.bluewizardhat.dockerwebapp.util;

import java.util.concurrent.RejectedExecutionException;

import javax.validation.ConstraintViolationException;

import org.springframework.core.task.TaskRejectedException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import lombok.extern.slf4j.Slf4j;
import net.bluewizardhat.dockerwebapp.domain.logic.exception.NotFoundException;

@Slf4j
@RestControllerAdvice
public class CustomResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(IllegalArgumentException.class)
	@ResponseStatus(code = HttpStatus.BAD_REQUEST)
	public void handleException(IllegalArgumentException e) {
		log.warn("Unhandled IllegalArgumentException: '{}'", e.getMessage());
	}

	@ExceptionHandler(ConstraintViolationException.class)
	@ResponseStatus(code = HttpStatus.BAD_REQUEST)
	public void handleException(ConstraintViolationException e) {
		log.warn("Unhandled ConstraintViolationException: '{}'", e.getMessage());
	}

	@ExceptionHandler(NotFoundException.class)
	@ResponseStatus(code = HttpStatus.NOT_FOUND)
	public void handleException(NotFoundException e) {
		log.warn("Unhandled NotFoundException: '{}'", e.getMessage());
	}

//	@ExceptionHandler(AsyncRequestTimeoutException.class)
//	@ResponseStatus(code = HttpStatus.REQUEST_TIMEOUT)
//	public void handleException(AsyncRequestTimeoutException e) {
//		log.error("Unhandled AsyncRequestTimeoutException: '{}'", e.getMessage(), e);
//	}

	@ExceptionHandler(TaskRejectedException.class)
	@ResponseStatus(code = HttpStatus.TOO_MANY_REQUESTS)
	public void handleException(TaskRejectedException e) {
		log.error("Unhandled TaskRejectedException: '{}'", e.getMessage(), e);
	}

	@ExceptionHandler(RejectedExecutionException.class)
	@ResponseStatus(code = HttpStatus.TOO_MANY_REQUESTS)
	public void handleException(RejectedExecutionException e) {
		log.error("Unhandled RejectedExecutionException: '{}'", e.getMessage(), e);
	}

	@ExceptionHandler(Exception.class)
	@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
	public void handleException(Exception e) {
		log.error("Unhandled {}: '{}'", e.getClass().getSimpleName(), e.getMessage(), e);
	}

}
