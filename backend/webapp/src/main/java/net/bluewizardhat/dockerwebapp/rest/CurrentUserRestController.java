package net.bluewizardhat.dockerwebapp.rest;

import static net.bluewizardhat.dockerwebapp.domain.logic.security.UserRoles.PRE_AUTH_USER;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import net.bluewizardhat.dockerwebapp.domain.logic.security.SecurityContextHelper;
import net.bluewizardhat.dockerwebapp.rest.data.LoginDetails;

@RestController
@RequestMapping("/api/public/user/current")
public class CurrentUserRestController extends BaseRestController {

	private static final ResponseEntity<LoginDetails> NO_CONTENT = ResponseEntity.status(HttpStatus.NO_CONTENT).build();

	@ApiResponses({
		@ApiResponse(code = 204, message = "If the user is not logged in")
	})
	@GetMapping(path = "/", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<LoginDetails> currentUser() {
		return SecurityContextHelper.getLoggedInUser()
				.map(u -> ResponseEntity.ok(
						new LoginDetails(u.getUser(),
								!SecurityContextHelper.getRoles().contains(PRE_AUTH_USER.getAuthority()))))
				.orElse(NO_CONTENT);
	}

	@GetMapping(path = "/roles", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public List<String> roles() {
		return SecurityContextHelper.getRoles().stream()
				.map(auth -> auth.getAuthority())
				.collect(Collectors.toList());
	}

}
