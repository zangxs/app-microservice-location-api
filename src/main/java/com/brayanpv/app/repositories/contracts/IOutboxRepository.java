package com.brayanpv.app.repositories.contracts;

import com.brayanpv.app.repositories.entities.OutboxEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface IOutboxRepository extends ReactiveCrudRepository<OutboxEntity, String> {

    Flux<OutboxEntity> findByStatus(String status);

}
