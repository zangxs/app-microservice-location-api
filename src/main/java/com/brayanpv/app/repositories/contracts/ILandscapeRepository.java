package com.brayanpv.app.repositories.contracts;

import com.brayanpv.app.repositories.entities.LandscapeEntity;
import com.brayanpv.app.repositories.entities.projection.LandscapeProjection;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public interface ILandscapeRepository extends ReactiveCrudRepository<LandscapeEntity, String> {

    @Query("UPDATE landscape SET \"status\" = $1, updated_at = now() WHERE id = $2::uuid")
    Mono<Void> updateStatus(String status, String landscapeId);


    @Query("""
    SELECT * FROM (
        SELECT *,
        (6371 * acos(
            cos(radians(:lat)) * cos(radians(latitude)) *
            cos(radians(longitude) - radians(:lng)) +
            sin(radians(:lat)) * sin(radians(latitude))
        )) AS distance
        FROM landscape
        WHERE status = 'APPROVED'
    ) AS subquery
    WHERE distance < :radius
    ORDER BY distance
    LIMIT 20
    """)
    Flux<LandscapeProjection> findNearby(Double lat, Double lng, Integer radius);

    @Query("SELECT * FROM landscape WHERE id = :id::uuid")
    Mono<LandscapeEntity> findById(UUID id);

    Flux<LandscapeEntity> findByStatusAndCreatedAtBefore(String status, LocalDateTime dateTime);

    Flux<LandscapeEntity> findByUserId(Long userId);

}
