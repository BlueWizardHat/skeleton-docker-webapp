package net.bluewizardhat.dockerwebapp.database.repositories;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import net.bluewizardhat.dockerwebapp.database.entities.User;

@Repository
public interface UserRepository extends ExtendedPagingAndSortingRepository<User, Long> {
	public Optional<User> findByLoginName(String login);
}
