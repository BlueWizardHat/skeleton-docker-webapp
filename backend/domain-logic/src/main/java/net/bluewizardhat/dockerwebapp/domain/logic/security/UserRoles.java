package net.bluewizardhat.dockerwebapp.domain.logic.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.google.common.collect.ImmutableList;

import lombok.Getter;

public enum UserRoles {
	/**
	 * Not Logged in (not used, here for completeness).
	 */
	ANONYMOUS,
	/**
	 * Used in case of Two-Factor authentication where the user has authenticated via username/password
	 * but has yet to authenticate via Two-Factor.
	 */
	PRE_AUTH_USER,
	/**
	 * Fully authenticated user.
	 */
	USER,
	/**
	 * Fully authenticated admin user.
	 */
	ADMIN
	;

	public static final ImmutableList<GrantedAuthority> PRE_AUTH_ROLES =
			ImmutableList.of(PRE_AUTH_USER.authority);

	public static final ImmutableList<GrantedAuthority> USER_ROLES =
			ImmutableList.of(USER.authority);

	public static final ImmutableList<GrantedAuthority> ADMIN_ROLES =
			ImmutableList.of(USER.authority, ADMIN.authority);

	@Getter
	private final String roleName;

	@Getter
	private final GrantedAuthority authority;

	private UserRoles() {
		roleName = "ROLE_" + name();
		authority = new SimpleGrantedAuthority(roleName);
	}

}
