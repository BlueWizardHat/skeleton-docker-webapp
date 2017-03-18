package net.bluewizardhat.dockerwebapp.rest;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class BrewingRestController extends BaseRestController {

	public static class TeapotException extends RuntimeException {
		private static final long serialVersionUID = -1972193502908131363L;

		public TeapotException(String message) {
			super(message);
		}
	}

	@ExceptionHandler(TeapotException.class)
	@ResponseStatus(code = HttpStatus.I_AM_A_TEAPOT)
	public String handleException(TeapotException e) {
		log.warn("Unhandled exception: {}", e.getMessage());
		return e.getMessage();
	}

	@ApiResponses({
		@ApiResponse(code = 418, message = "If the server is a tea pot", response = String.class)
	})
	@GetMapping(path = "/brewCoffee")
	public void brewCoffee() {
		throw new TeapotException("This server is not a coffe machine, it's a tea pot");
	}

	public static enum TeaType {
		EARL_GREY,
		FOREST_FRUIT
	}

	@Value
	public static class TeaCup {
		private TeaType content;
	}

	@GetMapping(path = "/brewTea/{teaType}")
	public TeaCup brewTea(@PathVariable("teaType") TeaType teaType) {
		return new TeaCup(teaType);
	}
}
