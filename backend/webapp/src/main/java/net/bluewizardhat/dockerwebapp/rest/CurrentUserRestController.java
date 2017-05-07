package net.bluewizardhat.dockerwebapp.rest;

import static net.bluewizardhat.dockerwebapp.domain.logic.security.UserRoles.PRE_AUTH_USER;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.bluewizardhat.dockerwebapp.domain.logic.security.SecurityContextHelper;
import net.bluewizardhat.dockerwebapp.rest.data.LoginDetails;

@RestController
@RequestMapping("/api/public/user/current")
public class CurrentUserRestController extends BaseRestController {

	private static final LoginDetails NO_USER = new LoginDetails(null, false);

	@GetMapping(path = "/", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public LoginDetails currentUser() {
		return SecurityContextHelper.getLoggedInUser()
				.map(u -> new LoginDetails(u, !SecurityContextHelper.getRoles().contains(PRE_AUTH_USER.getAuthority())))
				.orElse(NO_USER);
	}

//	/**
//	 * Returns the roles (mostly for debug)
//	 */
//	@GetMapping(path = "/roles", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
//	public List<String> roles() {
//		return SecurityContextHelper.getRoles().stream()
//				.map(auth -> auth.getAuthority())
//				.collect(Collectors.toList());
//	}

}
