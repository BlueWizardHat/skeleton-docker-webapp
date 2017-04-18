package net.bluewizardhat.dockerwebapp.util.security;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class PasswordHasherTest {

	@Test
	public void test() {
		String password = "dumdidum";

		String hash1 = PasswordHasher.hashPassword(password);
		String hash2 = PasswordHasher.hashPassword(password);

		assertNotEquals(hash1, hash2);
		assertTrue(PasswordHasher.verifyHash(hash1, password));
		assertTrue(PasswordHasher.verifyHash(hash2, password));
	}

}
