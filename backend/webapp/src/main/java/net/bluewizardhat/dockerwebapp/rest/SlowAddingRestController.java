package net.bluewizardhat.dockerwebapp.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/public/slowadd")
public class SlowAddingRestController extends BaseRestController {

	@GetMapping(path = "/slowadd")
	public DeferredResult<Integer> slowadd(int a, int b) {
		log.debug("Outside");
		return submit(() -> {
			log.debug("Before sleep");
			Thread.sleep(10000);
			log.debug("After sleep");
			return a + b;
		});
	}

	@GetMapping(path = "/fastadd")
	public int fastadd(int a, int b) {
		log.debug("main thread");
		return a + b;
	}

	@GetMapping(path = "/asyncfastadd")
	public DeferredResult<Integer> asynadd(int a, int b) {
		return submit(() -> {
			log.debug("in thread");
			return a + b;
		});
	}
}
