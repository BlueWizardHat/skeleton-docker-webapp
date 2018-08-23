package net.bluewizardhat.dockerwebapp.util.security;

import java.time.OffsetDateTime;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

import com.google.common.base.MoreObjects;

import lombok.extern.slf4j.Slf4j;
import net.bluewizardhat.dockerwebapp.database.entities.User;
import net.bluewizardhat.dockerwebapp.database.repositories.UserRepository;
import net.bluewizardhat.dockerwebapp.domain.logic.security.UserDetailsAdapter;

/**
 * This AuthenticationProvider registers both successsful and failed login attempts on the user entity.
 */
@Slf4j
public class CustomAuthenticationProvider extends DaoAuthenticationProvider {

	@Autowired
	private UserRepository userRepository;

	@Override
	@Transactional(dontRollbackOn = AuthenticationException.class)
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		return super.authenticate(authentication);
//		try {
//			Authentication result = super.authenticate(authentication);
//			return result;
//		} catch (AuthenticationException e) {
//			throw e;
//		}
	}

	@Override
	protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
		try {
			super.additionalAuthenticationChecks(userDetails, authentication);
			registerLoginSuccess(((UserDetailsAdapter) userDetails).getUser());
		} catch (AuthenticationException e) {
			registerLoginFailure(((UserDetailsAdapter) userDetails).getUser());
			throw e;
		}
	}

	private void registerLoginSuccess(User user) {
		user
			.setLastLogin(OffsetDateTime.now())
			.setLastLoginAttempt(null)
			.setFailedLoginAttempts(null);
		userRepository.save(user);
		log.debug("Registered successful login for user '{}'", user);
	}

	private void registerLoginFailure(User user) {
		user
			.setLastLoginAttempt(OffsetDateTime.now())
			.setFailedLoginAttempts(MoreObjects.firstNonNull(user.getFailedLoginAttempts(), 0) + 1);
		userRepository.save(user);
		log.debug("Registered failed login for user '{}'", user);
	}

}
