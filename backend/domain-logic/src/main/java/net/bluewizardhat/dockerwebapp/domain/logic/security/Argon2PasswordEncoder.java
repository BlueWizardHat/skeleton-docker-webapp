package net.bluewizardhat.dockerwebapp.domain.logic.security;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of {@linkplain PasswordEncoder} that uses the Argon2 password hashing algorithm.
 * Argon2 is the replacement for BCrypt.
 */
@Slf4j
@Component("argon2PasswordEncoder")
public class Argon2PasswordEncoder implements PasswordEncoder {
	private static final int iterations = 4;
	private static final int kibibytes = 65536;
	private static final int parallelism = 1;

	@Override
	public String encode(CharSequence rawPassword) {
		return argon2().hash(iterations, kibibytes, parallelism, rawPassword.toString());
	}

	@Override
	public boolean matches(CharSequence rawPassword, String encodedPassword) {
		boolean success = argon2().verify(encodedPassword, rawPassword.toString());
		log.trace("password match {}", success);
		return success;
	}

	private Argon2 argon2() {
		return Argon2Factory.create();
	}
}
