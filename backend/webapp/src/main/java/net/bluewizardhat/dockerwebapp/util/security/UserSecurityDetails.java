package net.bluewizardhat.dockerwebapp.util.security;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;

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
}
