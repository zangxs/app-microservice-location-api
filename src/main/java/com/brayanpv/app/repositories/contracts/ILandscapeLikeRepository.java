package com.brayanpv.app.repositories.contracts;

import com.brayanpv.app.repositories.entities.LandscapeLikeEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface ILandscapeLikeRepository extends ReactiveCrudRepository<LandscapeLikeEntity, UUID> {
    Mono<LandscapeLikeEntity> findByLandscapeIdAndUserId(UUID landscapeId, Long userId);
    Mono<Long> countByLandscapeId(UUID landscapeId);
    Mono<Void> deleteByLandscapeIdAndUserId(UUID landscapeId, Long userId);
}