package com.brayanpv.app.repositories.contracts;

import com.brayanpv.app.repositories.entities.LandscapeEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface ILandscapeRepository extends ReactiveCrudRepository<LandscapeEntity, String> {

    @Query("UPDATE landscape SET \"status\" = $1, updated_at = now() WHERE id = $2::uuid")
    Mono<Void> updateStatus(String status, String landscapeId);
}
