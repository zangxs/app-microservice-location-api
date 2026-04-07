package com.brayanpv.app.service.implementations;

import com.brayanpv.app.model.request.LandscapeRequest;
import com.brayanpv.app.model.response.LandscapeDetailResponse;
import com.brayanpv.app.model.response.LandscapeResponse;
import com.brayanpv.app.model.response.NearbyLandscapeResponse;
import com.brayanpv.app.repositories.contracts.ILandscapeLikeRepository;
import com.brayanpv.app.repositories.contracts.ILandscapeRepository;
import com.brayanpv.app.repositories.entities.LandscapeEntity;
import com.brayanpv.app.service.contracts.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Objects;
import java.util.UUID;

@Service
@Log4j2
@RequiredArgsConstructor
public class AppService implements IAppService {

    private final IS3Service s3Service;
    private final ILandscapeRepository landscapeRepository;
    private final IExifService exifService;
    private final IOutboxService outboxService;
    private final IIpService ipService;
    private final ILandscapeLikeRepository landscapeLikeRepository;

    @Value("${app.landscapes.max-radius}")
    private int maxRadius;

    @Value("${app.producer-url:http://localhost:8001}")
    private String producerUrl;

    @Value("${app.env:local}")
    private String env;

    @Override
    public Mono<LandscapeResponse> uploadFile(LandscapeRequest request) {
        log.info("request received: {}", request.toString());
        return Mono.deferContextual(ctx -> {
            String userId = ctx.get("userId");
            String email = ctx.get("email");

            return exifService.extractCoordinates(request.file())
                    .flatMap(exifResult -> {
                        if (exifResult.latitude() == null || exifResult.longitude() == null) {
                            return Mono.error(new IllegalArgumentException("Image does not contain GPS metadata"));
                        }
                        return s3Service.uploadFile(request.file(), exifResult.bytes())
                                .flatMap(imageUrl -> saveLandscapeAndCreateOutbox(request, userId, email, exifResult.latitude(), exifResult.longitude(), imageUrl));
                    });
        });
    }

    private Mono<LandscapeResponse> saveLandscapeAndCreateOutbox(
            LandscapeRequest request, String userId, String email, Double latitude, Double longitude, String imageUrl) {
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
                .flatMap(saved -> outboxService.publishLandscapeCreated(saved)
                        .thenReturn(new LandscapeResponse(saved.getId().toString(), "PENDING")));
    }

    @Override
    public Flux<NearbyLandscapeResponse> getNearby(Double lat, Double lng, Integer radius, String ip, String baseUrl) {
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
                            //projection.getImageUrl(),
                            buildProxyUrl(projection.getImageUrl(), baseUrl), // convierte a URL del proxy
                            projection.getDistance()
                    ));
        });
    }

    @Override
    public Mono<LandscapeDetailResponse> getLandscape(String id, String baseUrl) {
        return landscapeRepository.findById(UUID.fromString(id))
                .map(landscape -> new LandscapeDetailResponse(
                        landscape.getId().toString(),
                        landscape.getTitle(),
                        landscape.getDescription(),
                        landscape.getLatitude(),
                        landscape.getLongitude(),
                        buildProxyUrl(landscape.getImageUrl(), baseUrl), // convierte a URL del proxy
                        landscape.getStatus()
                ));
    }

    @Override
    public Mono<Boolean> hasLiked(String landscapeId, String userId) {
        return landscapeLikeRepository.existsByLandscapeIdAndUserId(
                UUID.fromString(landscapeId), Long.parseLong(userId));
    }

    @Override
    public Flux<LandscapeDetailResponse> getMyLandscapes(String userId, String baseUrl) {
        return landscapeRepository.findByUserId(Long.parseLong(userId))
                .map(landscape -> new LandscapeDetailResponse(
                        landscape.getId().toString(),
                        landscape.getTitle(),
                        landscape.getDescription(),
                        landscape.getLatitude(),
                        landscape.getLongitude(),
                        buildProxyUrl(landscape.getImageUrl(), baseUrl), // convierte a URL del proxy
                        landscape.getStatus()
                ));
    }


    private String buildProxyUrl(String imageUrl, String baseUrl) {
        if ("aws".equalsIgnoreCase(env)) {
            return imageUrl;
        }
        String filename = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
        if (Objects.isNull(baseUrl) || baseUrl.isEmpty()) {
            return producerUrl + "/app-microservice-location/images/" + filename;
        }
        return baseUrl + "/app-microservice-location/images/" + filename;
    }
}
