package net.bluewizardhat.dockerwebapp.database.repositories;

import java.io.Serializable;
import java.util.Optional;

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;

@NoRepositoryBean
public interface ExtendedPagingAndSortingRepository<T, ID extends Serializable> extends PagingAndSortingRepository<T, ID> {
	/**
	 * Like findOne(ID id) but returns an Optional.
	 */
	public Optional<T> findById(ID id);
}
