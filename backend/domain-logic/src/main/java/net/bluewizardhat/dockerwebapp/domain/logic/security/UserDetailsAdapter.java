package net.bluewizardhat.dockerwebapp.domain.logic.security;

import static java.lang.Math.min;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.bluewizardhat.dockerwebapp.database.entities.User;

@Slf4j
@RequiredArgsConstructor
public class UserDetailsAdapter implements UserDetails {
	private static final long serialVersionUID = 7659826888768628523L;

	/**
	 * Number of minutes to block logins after failed login attempts.
	 */
	private static final int[] delays_in_minutes = { 0, 0, 2, 5, 10, 30, 60, 3*60, 12*60, 24*60 };

	@Getter
	@NonNull
	private final User user;

	@Getter
	private final Collection<? extends GrantedAuthority> authorities = new ArrayList<>();

	public static Optional<UserDetailsAdapter> getLoggedInUser() {
		if (SecurityContextHolder.getContext() != null
				&& SecurityContextHolder.getContext().getAuthentication() != null
				&& SecurityContextHolder.getContext().getAuthentication().getPrincipal() != null
				&& SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof UserDetailsAdapter) {
			return Optional.of(((UserDetailsAdapter) SecurityContextHolder.getContext().getAuthentication().getPrincipal()));
		}

		return Optional.empty();
	}

	@Override
	public String getPassword() {
		return user.getHashedPassword();
	}

	@Override
	public String getUsername() {
		return user.getUserName();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		if (user.getFailedLoginAttempts() != null) {
			int delay = delays_in_minutes[min(user.getFailedLoginAttempts(), delays_in_minutes.length) - 1];
			OffsetDateTime earliestAllowedLoginTime = user.getLastLoginAttempt().plusMinutes(delay);
			if (earliestAllowedLoginTime.isAfter(OffsetDateTime.now())) {
				log.info("User '{}' is temporary locked until {}", user, earliestAllowedLoginTime);
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

}
