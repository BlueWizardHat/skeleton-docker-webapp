package net.bluewizardhat.dockerwebapp.util.security;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import net.bluewizardhat.dockerwebapp.database.entities.User;
import net.bluewizardhat.dockerwebapp.database.repositories.UserRepository;
import net.bluewizardhat.dockerwebapp.domain.logic.security.UserSecurityDetails;

@Slf4j
@Service
public class UserdetailsServiceImpl implements UserDetailsService {

	@Autowired
	private UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		log.debug("Attempting to load username '{}'", username);
		User user = userRepository.findByUserName(username)
				.orElseThrow(() -> new UsernameNotFoundException(username));

		log.debug("Loaded user '{}'", user);
		return new UserSecurityDetails(user, Collections.emptyList());
	}

}
