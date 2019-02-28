package net.bluewizardhat.dockerwebapp.rest;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import lombok.extern.slf4j.Slf4j;
import net.bluewizardhat.dockerwebapp.domain.logic.AsyncTestService;

@Slf4j
@RestController
@RequestMapping("/api/public/async")
public class AsyncTestController extends BaseRestController {

	@Autowired
	private AsyncTestService service;

	@GetMapping(path = "/spring", produces = MediaType.TEXT_PLAIN_VALUE)
	public @ResponseBody DeferredResult<String> doSpringAsync() {
		log.info("doSpringAsync: {}", SecurityContextHolder.getContext());
		return deferredResult(service.doSpringAsync(null, null));
	}

	@GetMapping(path = "/springTimeout", produces = MediaType.TEXT_PLAIN_VALUE)
	public @ResponseBody DeferredResult<String> doSpringAsyncTimeout() {
		log.info("doSpringAsync: {}", SecurityContextHolder.getContext());
		return deferredResult(service.doSpringAsync(null, null), 500L);
	}

	@GetMapping(path = "/logmdc", produces = MediaType.TEXT_PLAIN_VALUE)
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	public void logMdcContents() {
		Map<String, String> mdcCopy = new TreeMap<>(MDC.getCopyOfContextMap());
		log.info("MDC contains {} entries; security context {}", mdcCopy.size(), SecurityContextHolder.getContext());
		mdcCopy.forEach((key, value) -> log.info("MDC key='{}', value='{}'", key, value));
		service.logMdcContentsMeow();
	}

	@GetMapping(path = "/exception1", produces = MediaType.TEXT_PLAIN_VALUE)
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	public void throwException1() throws IOException {
		service.throwException1();
	}

	@GetMapping(path = "/exception2", produces = MediaType.TEXT_PLAIN_VALUE)
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	public void throwException2() {
		service.throwException2();
	}

	@GetMapping(path = "/exception3", produces = MediaType.TEXT_PLAIN_VALUE)
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	public void throwException3() {
		service.throwException3();
	}

	@GetMapping(path = "/exception4", produces = MediaType.TEXT_PLAIN_VALUE)
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	public void throwException4() {
		service.throwException4();
	}
}
