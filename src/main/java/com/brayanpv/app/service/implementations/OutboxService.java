package com.brayanpv.app.service.implementations;

import com.brayanpv.app.repositories.contracts.IOutboxRepository;
import com.brayanpv.app.repositories.entities.LandscapeEntity;
import com.brayanpv.app.repositories.entities.OutboxEntity;
import com.brayanpv.app.service.contracts.IOutboxService;
import com.brayanspv.library.model.events.LandscapeEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Service
@Log4j2
@RequiredArgsConstructor
public class OutboxService implements IOutboxService {

    private final IOutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> publishLandscapeCreated(LandscapeEntity landscape) {
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
            log.error("Error serializing LandscapeEvent for landscape {}", landscape.getId(), e);
            return Mono.error(new RuntimeException("Error serializing event", e));
        }

        OutboxEntity outboxEntity = OutboxEntity.builder()
                .aggregateId(UUID.fromString(landscape.getId().toString()))
                .eventType("LANDSCAPE_CREATED")
                .payload(payload)
                .status("PENDING")
                .retries(0)
                .maxRetries(3)
                .createdAt(LocalDateTime.now(ZoneOffset.UTC))
                .build();

        return outboxRepository.save(outboxEntity).then();
    }
}
