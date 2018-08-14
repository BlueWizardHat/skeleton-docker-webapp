package net.bluewizardhat.dockerwebapp.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.async.DeferredResult;

import lombok.extern.slf4j.Slf4j;
import net.bluewizardhat.dockerwebapp.domain.logic.AsyncTestService;

@Slf4j
@Component
@RequestMapping("/api/public/async")
public class AsyncTestController extends BaseRestController {

	@Autowired
	private AsyncTestService service;

	@GetMapping(path = "/spring", produces = MediaType.TEXT_PLAIN_VALUE)
	public @ResponseBody DeferredResult<String> doSpringAsync() {
		log.info("doSpringAsync: {}", SecurityContextHolder.getContext());
		return deferredResult(service.doSpringAsync());
	}

}
