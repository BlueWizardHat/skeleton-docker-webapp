package net.bluewizardhat.dockerwebapp.domain.logic.security;

import java.util.Collection;
import java.util.Optional;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import lombok.Getter;
import net.bluewizardhat.dockerwebapp.database.entities.User;

public class UserSecurityDetails extends org.springframework.security.core.userdetails.User {
	private static final long serialVersionUID = 7659826888768628523L;

	@Getter
	private User user;

	public UserSecurityDetails(User user, Collection<? extends GrantedAuthority> authorities) {
		super(user.getUserName(), user.getHashedPassword(), authorities);
		this.user = user;
	}

	public static Optional<User> getLoggedInUser() {
		if (SecurityContextHolder.getContext() != null
				&& SecurityContextHolder.getContext().getAuthentication() != null
				&& SecurityContextHolder.getContext().getAuthentication().getPrincipal() != null
				&& SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof UserSecurityDetails) {
			return Optional.of(((UserSecurityDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser());
		}

		return Optional.empty();
	}
}
