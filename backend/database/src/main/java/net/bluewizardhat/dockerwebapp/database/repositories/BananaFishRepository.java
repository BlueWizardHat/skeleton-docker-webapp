package net.bluewizardhat.dockerwebapp.database.repositories;

import org.springframework.stereotype.Repository;

import net.bluewizardhat.dockerwebapp.database.entities.BananaFish;

@Repository
public interface BananaFishRepository extends ExtendedPagingAndSortingRepository<BananaFish, Long> {
}
