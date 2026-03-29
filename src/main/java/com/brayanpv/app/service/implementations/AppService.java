package com.brayanpv.app.service.implementations;

import com.brayanpv.app.model.request.LandscapeRequest;
import com.brayanpv.app.model.response.LandscapeResponse;
import com.brayanpv.app.model.response.NearbyLandscapeResponse;
import com.brayanpv.app.repositories.contracts.ILandscapeRepository;
import com.brayanpv.app.repositories.contracts.IOutboxRepository;
import com.brayanpv.app.repositories.entities.LandscapeEntity;
import com.brayanpv.app.repositories.entities.OutboxEntity;
import com.brayanpv.app.service.contracts.*;
import com.brayanspv.library.model.events.LandscapeEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
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
    private final IExifService exifService;
    private final IOutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;
    private final IIpService ipService;

    @Value("${app.landscapes.max-radius}")
    private int maxRadius;

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
                                                        .aggregateId(UUID.fromString(saved.getId().toString()))
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

    @Override
    public Flux<NearbyLandscapeResponse> getNearby(Double lat, Double lng, Integer radius, String ip) {
        Mono<double[]> coordinatesMono = (lat != null && lng != null)
                ? Mono.just(new double[]{lat, lng})
                : ipService.getCoordinates(ip);

        return coordinatesMono.flatMapMany(coords -> {
            double resolvedLat = coords[0];
            double resolvedLng = coords[1];
            int resolvedRadius = Math.min(radius, maxRadius);

            return landscapeRepository.findNearby(resolvedLat, resolvedLng, resolvedRadius)
                    .map(projection -> new NearbyLandscapeResponse(
                            projection.getId().toString(),
                            projection.getTitle(),
                            projection.getDescription(),
                            projection.getLatitude(),
                            projection.getLongitude(),
                            projection.getImageUrl(),
                            projection.getDistance()
                    ));
        });
    }
}
