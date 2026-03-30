package com.brayanpv.app.scheduler.implementations;

import com.brayanpv.app.repositories.contracts.ILandscapeRepository;
import com.brayanpv.app.repositories.contracts.IOutboxRepository;
import com.brayanpv.app.repositories.entities.OutboxEntity;
import com.brayanpv.app.scheduler.contracts.IReconciliationScheduler;
import com.brayanspv.library.model.events.LandscapeEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Log4j2
public class ReconciliationScheduler implements IReconciliationScheduler {

    private final ILandscapeRepository landscapeRepository;
    private final IOutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    @Scheduled(fixedDelayString = "${app.outbox.reconciliation-delay:3600000}")
    @Override
    public void reconcile() {
        log.info("Running reconciliation scheduler");

        landscapeRepository.findByStatusAndCreatedAtBefore(
                        "PENDING",
                        LocalDateTime.now(ZoneOffset.UTC).minusHours(1)
                )
                .flatMap(landscape -> {
                    log.info("Reconciling landscape: {}", landscape.getId());

                    // Verificar que no tenga ya un outbox PENDING
                    return outboxRepository.findByAggregateIdAndStatus(
                                    UUID.fromString(landscape.getId()), "PENDING")
                            .hasElements()
                            .flatMap(hasPending -> {
                                if (hasPending) {
                                    log.info("Already has pending outbox: {}", landscape.getId());
                                    return Mono.empty();
                                }

                                LandscapeEvent event = new LandscapeEvent(
                                        landscape.getId().toString(),
                                        landscape.getUserId().toString(),
                                        null,
                                        landscape.getTitle(),
                                        landscape.getDescription(),
                                        landscape.getLatitude(),
                                        landscape.getLongitude(),
                                        landscape.getImageUrl()
                                );

                                String payload;
                                try {
                                    payload = objectMapper.writeValueAsString(event);
                                } catch (JsonProcessingException e) {
                                    return Mono.error(new RuntimeException("Error serializing"));
                                }

                                OutboxEntity outboxEntity = OutboxEntity.builder()
                                        .aggregateId(UUID.fromString(landscape.getId()))
                                        .eventType("LANDSCAPE_CREATED")
                                        .payload(payload)
                                        .status("PENDING")
                                        .retries(0)
                                        .maxRetries(3)
                                        .createdAt(LocalDateTime.now(ZoneOffset.UTC))
                                        .build();

                                return outboxRepository.save(outboxEntity).then();
                            });
                })
                .subscribe(
                        null,
                        error -> log.error("Error in reconciliation: {}", error.getMessage())
                );
    }
}
