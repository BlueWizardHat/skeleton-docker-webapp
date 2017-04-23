package net.bluewizardhat.dockerwebapp.rest;

import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.bluewizardhat.dockerwebapp.database.entities.User;
import net.bluewizardhat.dockerwebapp.util.security.UserSecurityDetails;

@RestController
@RequestMapping("/api/public")
public class WhoAmIRestController extends BaseRestController {

	@GetMapping(path = "/whoami", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public User whoami() {
		if (SecurityContextHolder.getContext() != null
				&& SecurityContextHolder.getContext().getAuthentication() != null
				&& SecurityContextHolder.getContext().getAuthentication().getPrincipal() != null
				&& SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof UserSecurityDetails) {
			return ((UserSecurityDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
		}

		return null;
	}

}
