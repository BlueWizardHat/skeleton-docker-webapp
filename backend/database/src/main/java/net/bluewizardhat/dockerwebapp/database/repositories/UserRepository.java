package net.bluewizardhat.dockerwebapp.database.repositories;

import java.util.Optional;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import net.bluewizardhat.dockerwebapp.database.entities.User;

@Repository
public interface UserRepository extends PagingAndSortingRepository<User, Long> {
	public Optional<User> findByUserName(String userName);
}
