package net.bluewizardhat.dockerwebapp.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import net.bluewizardhat.dockerwebapp.domain.logic.AddingService;

@RestController
public class AddingRestController extends BaseRestController {

	@Autowired
	private AddingService addingService;

	@GetMapping("/api/add")
	public DeferredResult<Integer> add(@RequestParam int a, @RequestParam int b) {
		return submit(() -> addingService.add(a, b));
	}
}
