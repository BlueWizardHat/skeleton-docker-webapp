package net.bluewizardhat.dockerwebapp.util.security;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import lombok.experimental.UtilityClass;

@UtilityClass
public class PasswordHasher {
	private static final int iterations = 4;
	private static final int kibibytes = 65536;
	private static final int parallelism = 1;

	public static String hashPassword(String password) {
		return argon2().hash(iterations, kibibytes, parallelism, password);
	}

	public static boolean verifyHash(String hash, String password) {
		return argon2().verify(hash, password);
	}

	private static Argon2 argon2() {
		return Argon2Factory.create();
	}
}
