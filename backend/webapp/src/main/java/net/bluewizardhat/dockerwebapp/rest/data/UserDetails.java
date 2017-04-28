package net.bluewizardhat.dockerwebapp.rest.data;

import java.util.List;

import lombok.Value;
import net.bluewizardhat.dockerwebapp.database.entities.User;

@Value
public class UserDetails {
	private final User user;
	private final List<String> roles;
}
