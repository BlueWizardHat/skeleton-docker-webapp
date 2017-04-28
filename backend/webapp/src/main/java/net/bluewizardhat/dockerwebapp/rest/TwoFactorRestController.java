package net.bluewizardhat.dockerwebapp.rest;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import net.bluewizardhat.dockerwebapp.database.entities.User;
import net.bluewizardhat.dockerwebapp.database.entities.User.UserType;
import net.bluewizardhat.dockerwebapp.domain.logic.security.SecurityContextHelper;

@Slf4j
@RestController
@RequestMapping("/login")
public class TwoFactorRestController extends BaseRestController {

	/**
	 * Handles TOTP (Timebased One-Time-Passkey) authentication using Google Authenticator compatible codes.
	 */
	@PostMapping(path = "/totp")
	public void totp(@RequestBody String otp, @RequestParam(name = "_csrf", required = false) String _csrf) {
		// TODO validate otp
		grantAuthorities();
	}

	private void grantAuthorities() {
		User user = SecurityContextHelper.getLoggedInUser().orElseThrow(IllegalStateException::new).getUser();
		if (user.getType() == UserType.ADMIN) {
			SecurityContextHelper.grantAdminAuthority();
			log.debug("Granted user 'admin' roles");
		} else {
			SecurityContextHelper.grantUserAuthority();
			log.debug("granted user 'user' roles");
		}
	}
}
