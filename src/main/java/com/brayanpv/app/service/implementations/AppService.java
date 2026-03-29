package com.brayanpv.app.service.implementations;

import com.brayanpv.app.model.request.LandscapeRequest;
import com.brayanpv.app.model.response.LandscapeResponse;
import com.brayanpv.app.repositories.contracts.ILandscapeRepository;
import com.brayanpv.app.repositories.contracts.IOutboxRepository;
import com.brayanpv.app.repositories.entities.LandscapeEntity;
import com.brayanpv.app.repositories.entities.OutboxEntity;
import com.brayanpv.app.service.contracts.IAppService;
import com.brayanpv.app.service.contracts.IExifService;
import com.brayanpv.app.service.contracts.IRabbitMQService;
import com.brayanpv.app.service.contracts.IS3Service;
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
public class AppService implements IAppService {

    private final IS3Service s3Service;
    private final ILandscapeRepository landscapeRepository;
    private final IRabbitMQService rabbitMQService;
    private final IExifService exifService;
    private final IOutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    @Override
    public Mono<LandscapeResponse> uploadFile(LandscapeRequest request) {
        log.info("request received: {}", request.toString());
        return Mono.deferContextual(ctx -> {
            String userId = ctx.get("userId");
            return exifService.extractCoordinates(request.file())
                    .flatMap(exifResult -> {
                        Double latitude = exifResult.latitude() != null ? exifResult.latitude() : request.latitude();
                        Double longitude = exifResult.longitude() != null ? exifResult.longitude() : request.longitude();

                        return s3Service.uploadFile(request.file(), exifResult.bytes())
                                .flatMap(imageUrl -> {
                                    LandscapeEntity entity = LandscapeEntity.builder()
                                            .userId(Long.parseLong(userId))
                                            .title(request.title())
                                            .description(request.description())
                                            .latitude(latitude)
                                            .longitude(longitude)
                                            .imageUrl(imageUrl)
                                            .status("PENDING")
                                            .createdAt(LocalDateTime.now(ZoneOffset.UTC))
                                            .updatedAt(LocalDateTime.now(ZoneOffset.UTC))
                                            .build();

                                    return landscapeRepository.save(entity)
                                            .flatMap(saved -> {
                                                LandscapeEvent event = new LandscapeEvent(
                                                        saved.getId().toString(),
                                                        userId,
                                                        ctx.get("email"),
                                                        saved.getTitle(),
                                                        saved.getDescription(),
                                                        saved.getLatitude(),
                                                        saved.getLongitude(),
                                                        saved.getImageUrl()
                                                );

                                                String payload;
                                                try {
                                                    payload = objectMapper.writeValueAsString(event);
                                                } catch (JsonProcessingException e) {
                                                    return Mono.error(new RuntimeException("Error serializing event"));
                                                }

                                                OutboxEntity outboxEntity = OutboxEntity.builder()
                                                        .aggregateId(saved.getId().toString())
                                                        .eventType("LANDSCAPE_CREATED")
                                                        .payload(payload)
                                                        .status("PENDING")
                                                        .retries(0)
                                                        .maxRetries(3)
                                                        .createdAt(LocalDateTime.now(ZoneOffset.UTC))
                                                        .build();

                                                return outboxRepository.save(outboxEntity)
                                                        .thenReturn(new LandscapeResponse(saved.getId().toString(), "PENDING"));
                                            });
                                });
                    });
        });
    }
}
