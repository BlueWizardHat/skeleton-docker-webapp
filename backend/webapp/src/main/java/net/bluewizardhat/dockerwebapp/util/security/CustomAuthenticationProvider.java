package net.bluewizardhat.dockerwebapp.util.security;

import java.time.OffsetDateTime;
import java.util.Random;

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
 * This AuthenticationProvider extends DaoAuthenticationProvider with a login delay to prevent
 * user guessing. And also registers both successsful and failed login attempts on the user entity.
 */
@Slf4j
public class CustomAuthenticationProvider extends DaoAuthenticationProvider {

	private static final int min_login_time = 1500;
	private static final int max_login_random_delay = 1500;

	private Random random = new Random();

	@Autowired
	private UserRepository userRepository;

	@Override
	@Transactional(dontRollbackOn = AuthenticationException.class)
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		long startTime = System.currentTimeMillis();
		try {
			Authentication result = super.authenticate(authentication);
			delay(startTime);
			return result;
		} catch (AuthenticationException e) {
			delay(startTime);
			throw e;
		}
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

	/**
	 * Introduces an artificial delay in the login process. That way an attacker cannot
	 * determine if a particular login exists simply by measuring the response time.
	 */
	private void delay(long startTime) {
		long time = System.currentTimeMillis() - startTime;
		long delay = min_login_time - time + random.nextInt(max_login_random_delay);
		if (delay > 0) {
			log.debug("Login took {} milliseconds (actual), delaying for {} (total {})", time, delay, time+delay);
			try {
				Thread.sleep(delay);
			} catch (InterruptedException e) {
			}
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
