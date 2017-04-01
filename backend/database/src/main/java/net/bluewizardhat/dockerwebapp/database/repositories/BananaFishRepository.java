package net.bluewizardhat.dockerwebapp.database.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import net.bluewizardhat.dockerwebapp.database.entities.BananaFish;

@Repository
public interface BananaFishRepository extends PagingAndSortingRepository<BananaFish, Long> {
}
