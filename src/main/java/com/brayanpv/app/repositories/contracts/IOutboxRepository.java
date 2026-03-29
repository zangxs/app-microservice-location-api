package com.brayanpv.app.repositories.contracts;

import com.brayanpv.app.repositories.entities.OutboxEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public interface IOutboxRepository extends ReactiveCrudRepository<OutboxEntity, String> {

    Flux<OutboxEntity> findByStatus(String status);

    @Query("UPDATE outbox SET status = :status, retries = :retries, processed_at = :processedAt WHERE id = :id")
    Mono<Void> updateOutbox(UUID id, String status, int retries, LocalDateTime processedAt);

}
