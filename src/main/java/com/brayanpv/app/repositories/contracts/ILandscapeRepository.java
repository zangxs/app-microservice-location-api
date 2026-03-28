package com.brayanpv.app.repositories.contracts;

import com.brayanpv.app.repositories.entities.LandscapeEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ILandscapeRepository extends ReactiveCrudRepository<LandscapeEntity, String> {
}
