package net.bluewizardhat.dockerwebapp.util.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public interface UserRoles {
	/**
	 * Used in case of Two-Factor authentication where the user has authenticated via username/password
	 * but has yet to authenticate via Two-Factor.
	 */
	public static final GrantedAuthority PRE_AUTH_ROLE = new SimpleGrantedAuthority("ROLE_PRE_AUTH_USER");
	/**
	 * Fully authenticated user.
	 */
	public static final GrantedAuthority USER_ROLE = new SimpleGrantedAuthority("USER_ROLE");
}
