package net.bluewizardhat.dockerwebapp.domain.logic.security;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

public class Argon2PasswordEncoderTest {

	private PasswordEncoder passwordEncoder = new Argon2PasswordEncoder();

	@Test
	@Ignore // - this test takes a long time
	public void test() {
		String password = "dumdidum";

		String hash1 = passwordEncoder.encode(password);
		String hash2 = passwordEncoder.encode(password);

		assertNotEquals(hash1, hash2);
		assertTrue(passwordEncoder.matches(password, hash1));
		assertTrue(passwordEncoder.matches(password, hash2));
	}

}
