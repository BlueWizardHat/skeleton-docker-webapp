package net.bluewizardhat.dockerwebapp.domain.logic.security;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.Assert;

import lombok.experimental.UtilityClass;

@UtilityClass
public class SecurityContextHelper {

	/**
	 * Returns details about the currently logged in user if any.
	 */
	public static Optional<UserDetailsAdapter> getLoggedInUser() {
		if (SecurityContextHolder.getContext().getAuthentication() != null
				&& SecurityContextHolder.getContext().getAuthentication().getPrincipal() != null
				&& SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof UserDetailsAdapter) {
			return Optional.of(((UserDetailsAdapter) SecurityContextHolder.getContext().getAuthentication().getPrincipal()));
		}

		return Optional.empty();
	}

	/**
	 * Returns details about the currently logged in user if any.
	 */
	@SuppressWarnings("unchecked")
	public static List<GrantedAuthority> getRoles() {
		if (SecurityContextHolder.getContext().getAuthentication() != null) {
			return (List<GrantedAuthority>) SecurityContextHolder.getContext().getAuthentication().getAuthorities();
		}

		return Collections.emptyList();
	}

	/**
	 * Grants the user role to the currently logged in user.
	 */
	public static void grantUserAuthority() {
		grantAuthorities(UserRoles.USER_ROLES);
	}

	/**
	 * Grants the admin roles to the currently logged in user.
	 */
	public static void grantAdminAuthority() {
		grantAuthorities(UserRoles.ADMIN_ROLES);
	}

	private static void grantAuthorities(List<GrantedAuthority> authorities) {
		Assert.state(getLoggedInUser().isPresent(), "Cannot grant authorities if not authenticated");
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Authentication newAuth = new UsernamePasswordAuthenticationToken(auth.getPrincipal(), auth.getCredentials(), authorities);
		SecurityContextHolder.getContext().setAuthentication(newAuth);
	}

}
