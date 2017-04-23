package net.bluewizardhat.dockerwebapp.domain.logic;

import java.time.OffsetDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import net.bluewizardhat.dockerwebapp.database.entities.User;
import net.bluewizardhat.dockerwebapp.database.repositories.UserRepository;
import net.bluewizardhat.dockerwebapp.domain.logic.exception.NotFoundException;
import net.bluewizardhat.dockerwebapp.domain.model.UserModels.UserCreateRequest;
import net.bluewizardhat.dockerwebapp.domain.model.UserModels.UserPasswordUpdateRequest;
import net.bluewizardhat.dockerwebapp.domain.model.UserModels.UserUpdateRequest;

@Slf4j
@Component
public class UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	@Qualifier("argon2PasswordEncoder")
	private PasswordEncoder passwordEncoder;

	public Optional<User> findByuserName(String userName) {
		return userRepository.findByUserName(userName);
	}

	public User createUser(UserCreateRequest request) {
		User user = new User()
				.setCreated(OffsetDateTime.now())
				.setUserName(request.getUserName())
				.setDisplayName(request.getDisplayName())
				.setEmail(request.getEmail())
				.setHashedPassword(passwordEncoder.encode(request.getPassword()))
				;

		user = userRepository.save(user);
		log.info("Created new user='{}'", user);
		return user;
	}

	public User updateUser(UserUpdateRequest request) {
		User user = userRepository.findByUserName(request.getUserName())
				.orElseThrow(() -> new NotFoundException(User.class, request.getUserName()));

		user.setDisplayName(request.getDisplayName());

		log.info("Updating displayname for user='{}' - '{}'", request.getUserName(), request.getDisplayName());
		return userRepository.save(user);
	}

	public boolean updatePassword(UserPasswordUpdateRequest request) {
		User user = userRepository.findByUserName(request.getUserName())
				.orElseThrow(() -> new NotFoundException(User.class, request.getUserName()));

		if (passwordEncoder.matches(request.getOldPassword(), user.getHashedPassword())) {
			user.setHashedPassword(passwordEncoder.encode(request.getNewPassword()));
			userRepository.save(user);
			log.info("Updated password for user='{}'", request.getUserName());
			return true;
		}

		log.warn("Did not update password for user='{}', password validation failed", request.getUserName());
		return false;
	}

}
