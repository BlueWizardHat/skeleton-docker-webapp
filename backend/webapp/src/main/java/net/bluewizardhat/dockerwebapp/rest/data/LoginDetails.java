package net.bluewizardhat.dockerwebapp.rest.data;

import lombok.Value;
import net.bluewizardhat.dockerwebapp.database.entities.User;

@Value
public class LoginDetails {
	private final User user;
	private boolean fullyAuthenticated;
}
