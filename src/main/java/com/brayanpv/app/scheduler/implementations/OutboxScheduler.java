package com.brayanpv.app.scheduler.implementations;

import com.brayanpv.app.repositories.contracts.IOutboxRepository;
import com.brayanpv.app.repositories.entities.OutboxEntity;
import com.brayanpv.app.scheduler.contracts.IOutboxScheduler;
import com.brayanpv.app.service.contracts.IRabbitMQService;
import com.brayanspv.library.model.events.LandscapeEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Component
@RequiredArgsConstructor
@Log4j2
public class OutboxScheduler implements IOutboxScheduler {
    private final IOutboxRepository outboxRepository;
    private final IRabbitMQService rabbitMQService;
    private final ObjectMapper objectMapper;

    @Override
    @Scheduled(fixedDelayString = "${app.outbox.scheduler-delay:5000}")
    public void processOutbox() {
        outboxRepository.findByStatus("PENDING")
                .filter(outbox -> outbox.getRetries() < outbox.getMaxRetries())
                .flatMap(this::processEvent)
                .subscribe();
    }

    private Mono<Void> processEvent(OutboxEntity outbox) {
        return Mono.fromCallable(() -> objectMapper.readValue(outbox.getPayload(), LandscapeEvent.class))
                .flatMap(event -> rabbitMQService.publishLandscape(event)
                        .then(markAsProcessed(outbox))
                        .onErrorResume(e -> {
                            log.error("Error publishing event: {}, retries: {}", outbox.getId(), outbox.getRetries());
                            return incrementRetries(outbox);
                        })
                );
    }

    private Mono<Void> markAsProcessed(OutboxEntity outbox) {
        return outboxRepository.updateOutbox(
                outbox.getId(),
                "PROCESSED",
                outbox.getRetries(),
                LocalDateTime.now(ZoneOffset.UTC)
        );
    }

    private Mono<Void> incrementRetries(OutboxEntity outbox) {
        return outboxRepository.updateOutbox(
                outbox.getId(),
                "PENDING",
                outbox.getRetries() + 1,
                null
        );
    }

}
