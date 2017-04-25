package net.bluewizardhat.dockerwebapp.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import net.bluewizardhat.dockerwebapp.database.entities.User;
import net.bluewizardhat.dockerwebapp.domain.logic.security.UserDetailsAdapter;

@RestController
@RequestMapping("/api/public")
public class WhoAmIRestController extends BaseRestController {

	private static final ResponseEntity<User> NO_CONTENT = ResponseEntity.status(HttpStatus.NO_CONTENT).build();

	@ApiResponses({
		@ApiResponse(code = 204, message = "If the user is not logged in")
	})
	@GetMapping(path = "/whoami", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<User> whoami() {
		return UserDetailsAdapter.getLoggedInUser()
				.map(u -> ResponseEntity.ok(u.getUser()))
				.orElse(NO_CONTENT);
	}

}
