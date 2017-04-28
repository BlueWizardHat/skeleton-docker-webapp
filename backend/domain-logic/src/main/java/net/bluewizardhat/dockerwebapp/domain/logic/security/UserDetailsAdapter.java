package net.bluewizardhat.dockerwebapp.domain.logic.security;

import static java.lang.Math.min;

import java.time.OffsetDateTime;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.google.common.collect.ImmutableList;

import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import net.bluewizardhat.dockerwebapp.database.entities.User;
import net.bluewizardhat.dockerwebapp.database.entities.User.UserState;
import net.bluewizardhat.dockerwebapp.database.entities.User.UserType;

@Slf4j
@ToString
public class UserDetailsAdapter implements UserDetails {
	private static final long serialVersionUID = 7659826888768628523L;

	/**
	 * Number of minutes to block logins after failed login attempts.
	 */
	private static final int[] delays_in_minutes = { 0, 0, 2, 5, 10, 30, 60, 3*60, 12*60, 24*60 };

	@Getter
	private final User user;

	@Getter
	private final ImmutableList<GrantedAuthority> authorities;

	public UserDetailsAdapter(@NonNull User user) {
		this.user = user;
		authorities = user.getTwoFactorDetails() != null ?
				UserRoles.PRE_AUTH_ROLES :
					user.getType() == UserType.ADMIN ? UserRoles.ADMIN_ROLES : UserRoles.USER_ROLES;
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
		return user.getState() == UserState.ACTIVE;
	}

}
