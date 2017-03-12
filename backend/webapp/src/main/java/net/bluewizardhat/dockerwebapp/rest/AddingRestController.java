package net.bluewizardhat.dockerwebapp.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AddingRestController {

	@GetMapping("/api/add")
	public int add(@RequestParam int a, @RequestParam int b) {
		return a + b;
	}
}
