package com.brayanpv.app.service.implementations;

import com.brayanpv.app.model.ExifResult;
import com.brayanpv.app.model.request.LandscapeRequest;
import com.brayanpv.app.model.response.LandscapeDetailResponse;
import com.brayanpv.app.model.response.LandscapeResponse;
import com.brayanpv.app.model.response.NearbyLandscapeResponse;
import com.brayanpv.app.repositories.contracts.ILandscapeLikeRepository;
import com.brayanpv.app.repositories.contracts.ILandscapeRepository;
import com.brayanpv.app.repositories.contracts.IOutboxRepository;
import com.brayanpv.app.repositories.entities.LandscapeEntity;
import com.brayanpv.app.repositories.entities.OutboxEntity;
import com.brayanpv.app.repositories.entities.projection.LandscapeProjection;
import com.brayanpv.app.service.contracts.IExifService;
import com.brayanpv.app.service.contracts.IIpService;
import com.brayanpv.app.service.contracts.IS3Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppServiceTest {

    @Mock
    private IS3Service s3Service;

    @Mock
    private ILandscapeRepository landscapeRepository;

    @Mock
    private IExifService exifService;

    @Mock
    private IOutboxRepository outboxRepository;

    @Mock
    private IIpService ipService;

    @Mock
    private ILandscapeLikeRepository landscapeLikeRepository;

    @Mock
    private FilePart mockFilePart;

    private ObjectMapper objectMapper;

    private AppService appService;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        appService = new AppService(
                s3Service,
                landscapeRepository,
                exifService,
                outboxRepository,
                objectMapper,
                ipService,
                landscapeLikeRepository
        );
        ReflectionTestUtils.setField(appService, "maxRadius", 100);
        ReflectionTestUtils.setField(appService, "producerUrl", "http://localhost:8001");
    }

    @Test
    void uploadFileSuccess() {
        String userId = "123";
        String email = "test@example.com";
        String landscapeId = UUID.randomUUID().toString();
        LandscapeRequest request = new LandscapeRequest(
                mockFilePart, "Test Title", "Test Desc", 10.0, 20.0
        );

        byte[] imageBytes = new byte[]{1, 2, 3};
        ExifResult exifResult = new ExifResult(imageBytes, null, null);

        when(exifService.extractCoordinates(mockFilePart)).thenReturn(Mono.just(exifResult));
        when(s3Service.uploadFile(mockFilePart, imageBytes)).thenReturn(Mono.just("http://minio/bucket/file.jpg"));

        LandscapeEntity savedEntity = LandscapeEntity.builder()
                .id(landscapeId)
                .userId(123L)
                .title("Test Title")
                .description("Test Desc")
                .latitude(10.0)
                .longitude(20.0)
                .imageUrl("http://minio/bucket/file.jpg")
                .status("PENDING")
                .createdAt(LocalDateTime.now())
                .build();

        when(landscapeRepository.save(any(LandscapeEntity.class))).thenReturn(Mono.just(savedEntity));

        OutboxEntity savedOutbox = OutboxEntity.builder()
                .id(UUID.randomUUID())
                .aggregateId(UUID.fromString(landscapeId))
                .eventType("LANDSCAPE_CREATED")
                .payload("{}")
                .status("PENDING")
                .retries(0)
                .maxRetries(3)
                .createdAt(LocalDateTime.now())
                .build();

        when(outboxRepository.save(any(OutboxEntity.class))).thenReturn(Mono.just(savedOutbox));

        Mono<LandscapeResponse> result = appService.uploadFile(request)
                .contextWrite(ctx -> ctx.put("userId", userId).put("email", email));

        StepVerifier.create(result)
                .assertNext(response -> {
                    assert response.getId().equals(landscapeId);
                    assert response.getStatus().equals("PENDING");
                })
                .verifyComplete();

        verify(exifService, times(1)).extractCoordinates(mockFilePart);
        verify(s3Service, times(1)).uploadFile(mockFilePart, imageBytes);
        verify(landscapeRepository, times(1)).save(any(LandscapeEntity.class));
        verify(outboxRepository, times(1)).save(any(OutboxEntity.class));
    }

    @Test
    void uploadFileWithExifCoordinates() {
        String userId = "123";
        String email = "test@example.com";
        String landscapeId = UUID.randomUUID().toString();
        LandscapeRequest request = new LandscapeRequest(
                mockFilePart, "Test Title", "Test Desc", 10.0, 20.0
        );

        byte[] imageBytes = new byte[]{1, 2, 3};
        ExifResult exifResult = new ExifResult(imageBytes, 4.5709, -74.2973);

        when(exifService.extractCoordinates(mockFilePart)).thenReturn(Mono.just(exifResult));
        when(s3Service.uploadFile(mockFilePart, imageBytes)).thenReturn(Mono.just("http://minio/bucket/file.jpg"));

        LandscapeEntity savedEntity = LandscapeEntity.builder()
                .id(landscapeId)
                .userId(123L)
                .title("Test Title")
                .description("Test Desc")
                .latitude(4.5709)
                .longitude(-74.2973)
                .imageUrl("http://minio/bucket/file.jpg")
                .status("PENDING")
                .createdAt(LocalDateTime.now())
                .build();

        when(landscapeRepository.save(any(LandscapeEntity.class))).thenReturn(Mono.just(savedEntity));

        OutboxEntity savedOutbox = OutboxEntity.builder()
                .id(UUID.randomUUID())
                .aggregateId(UUID.fromString(landscapeId))
                .eventType("LANDSCAPE_CREATED")
                .payload("{}")
                .status("PENDING")
                .retries(0)
                .maxRetries(3)
                .createdAt(LocalDateTime.now())
                .build();

        when(outboxRepository.save(any(OutboxEntity.class))).thenReturn(Mono.just(savedOutbox));

        Mono<LandscapeResponse> result = appService.uploadFile(request)
                .contextWrite(ctx -> ctx.put("userId", userId).put("email", email));

        StepVerifier.create(result)
                .assertNext(response -> {
                    assert response.getStatus().equals("PENDING");
                })
                .verifyComplete();

        verify(landscapeRepository).save(argThat(entity ->
                entity.getLatitude().equals(4.5709) && entity.getLongitude().equals(-74.2973)
        ));
    }

    @Test
    void getNearbyWithCoordinates() {
        LandscapeProjection projection = mock(LandscapeProjection.class);
        when(projection.getId()).thenReturn(UUID.randomUUID());
        when(projection.getTitle()).thenReturn("Test Landscape");
        when(projection.getDescription()).thenReturn("Description");
        when(projection.getLatitude()).thenReturn(10.0);
        when(projection.getLongitude()).thenReturn(20.0);
        when(projection.getImageUrl()).thenReturn("http://minio/bucket/test.jpg");
        when(projection.getDistance()).thenReturn(1.5);

        when(landscapeRepository.findNearby(10.0, 20.0, 50))
                .thenReturn(Flux.just(projection));

        Flux<NearbyLandscapeResponse> result = appService.getNearby(
                10.0, 20.0, 50, "8.8.8.8", "http://localhost:8080"
        );

        StepVerifier.create(result)
                .assertNext(response -> {
                    assert response.title().equals("Test Landscape");
                    assert response.latitude().equals(10.0);
                    assert response.longitude().equals(20.0);
                    assert response.distance().equals(1.5);
                    assert response.imageUrl().contains("test.jpg");
                })
                .verifyComplete();

        verify(ipService, never()).getCoordinates(anyString());
    }

    @Test
    void getNearbyWithoutCoordinatesUsesIpService() {
        when(ipService.getCoordinates("8.8.8.8"))
                .thenReturn(Mono.just(new double[]{4.5709, -74.2973}));

        LandscapeProjection projection = mock(LandscapeProjection.class);
        when(projection.getId()).thenReturn(UUID.randomUUID());
        when(projection.getTitle()).thenReturn("Nearby");
        when(projection.getDescription()).thenReturn("Desc");
        when(projection.getLatitude()).thenReturn(4.5709);
        when(projection.getLongitude()).thenReturn(-74.2973);
        when(projection.getImageUrl()).thenReturn("http://minio/bucket/img.jpg");
        when(projection.getDistance()).thenReturn(0.5);

        when(landscapeRepository.findNearby(4.5709, -74.2973, 50))
                .thenReturn(Flux.just(projection));

        Flux<NearbyLandscapeResponse> result = appService.getNearby(
                null, null, 50, "8.8.8.8", "http://localhost:8080"
        );

        StepVerifier.create(result)
                .assertNext(response -> {
                    assert response.title().equals("Nearby");
                })
                .verifyComplete();

        verify(ipService, times(1)).getCoordinates("8.8.8.8");
    }

    @Test
    void getNearbyRadiusCappedToMaxRadius() {
        when(landscapeRepository.findNearby(eq(10.0), eq(20.0), eq(100)))
                .thenReturn(Flux.empty());

        Flux<NearbyLandscapeResponse> result = appService.getNearby(
                10.0, 20.0, 200, "8.8.8.8", "http://localhost:8080"
        );

        StepVerifier.create(result)
                .verifyComplete();

        verify(landscapeRepository).findNearby(10.0, 20.0, 100);
    }

    @Test
    void getNearbyEmptyResult() {
        when(landscapeRepository.findNearby(10.0, 20.0, 50))
                .thenReturn(Flux.empty());

        Flux<NearbyLandscapeResponse> result = appService.getNearby(
                10.0, 20.0, 50, "8.8.8.8", "http://localhost:8080"
        );

        StepVerifier.create(result)
                .verifyComplete();
    }

    @Test
    void getLandscapeSuccess() {
        String landscapeId = UUID.randomUUID().toString();
        LandscapeEntity entity = LandscapeEntity.builder()
                .id(landscapeId)
                .title("My Landscape")
                .description("Description")
                .latitude(10.0)
                .longitude(20.0)
                .imageUrl("http://minio/bucket/photo.jpg")
                .status("APPROVED")
                .build();

        when(landscapeRepository.findById(UUID.fromString(landscapeId)))
                .thenReturn(Mono.just(entity));

        Mono<LandscapeDetailResponse> result = appService.getLandscape(
                landscapeId, "http://localhost:8080"
        );

        StepVerifier.create(result)
                .assertNext(response -> {
                    assert response.id().equals(landscapeId);
                    assert response.title().equals("My Landscape");
                    assert response.status().equals("APPROVED");
                    assert response.imageUrl().contains("photo.jpg");
                })
                .verifyComplete();
    }

    @Test
    void getLandscapeNotFound() {
        String landscapeId = UUID.randomUUID().toString();

        when(landscapeRepository.findById(UUID.fromString(landscapeId)))
                .thenReturn(Mono.empty());

        Mono<LandscapeDetailResponse> result = appService.getLandscape(
                landscapeId, "http://localhost:8080"
        );

        StepVerifier.create(result)
                .verifyComplete();
    }

    @Test
    void getLandscapeWithNullBaseUrl() {
        String landscapeId = UUID.randomUUID().toString();
        LandscapeEntity entity = LandscapeEntity.builder()
                .id(landscapeId)
                .title("Test")
                .description("Desc")
                .latitude(10.0)
                .longitude(20.0)
                .imageUrl("http://minio/bucket/photo.jpg")
                .status("APPROVED")
                .build();

        when(landscapeRepository.findById(UUID.fromString(landscapeId)))
                .thenReturn(Mono.just(entity));

        Mono<LandscapeDetailResponse> result = appService.getLandscape(landscapeId, null);

        StepVerifier.create(result)
                .assertNext(response -> {
                    assert response.imageUrl().startsWith("http://localhost:8001");
                    assert response.imageUrl().contains("photo.jpg");
                })
                .verifyComplete();
    }

    @Test
    void getLandscapeWithEmptyBaseUrl() {
        String landscapeId = UUID.randomUUID().toString();
        LandscapeEntity entity = LandscapeEntity.builder()
                .id(landscapeId)
                .title("Test")
                .description("Desc")
                .latitude(10.0)
                .longitude(20.0)
                .imageUrl("http://minio/bucket/photo.jpg")
                .status("APPROVED")
                .build();

        when(landscapeRepository.findById(UUID.fromString(landscapeId)))
                .thenReturn(Mono.just(entity));

        Mono<LandscapeDetailResponse> result = appService.getLandscape(landscapeId, "");

        StepVerifier.create(result)
                .assertNext(response -> {
                    assert response.imageUrl().startsWith("http://localhost:8001");
                    assert response.imageUrl().contains("photo.jpg");
                })
                .verifyComplete();
    }

    @Test
    void hasLikedTrue() {
        String landscapeId = UUID.randomUUID().toString();
        String userId = "123";

        when(landscapeLikeRepository.existsByLandscapeIdAndUserId(
                UUID.fromString(landscapeId), 123L))
                .thenReturn(Mono.just(true));

        Mono<Boolean> result = appService.hasLiked(landscapeId, userId);

        StepVerifier.create(result)
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void hasLikedFalse() {
        String landscapeId = UUID.randomUUID().toString();
        String userId = "456";

        when(landscapeLikeRepository.existsByLandscapeIdAndUserId(
                UUID.fromString(landscapeId), 456L))
                .thenReturn(Mono.just(false));

        Mono<Boolean> result = appService.hasLiked(landscapeId, userId);

        StepVerifier.create(result)
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    void getMyLandscapesSuccess() {
        String userId = "123";

        LandscapeEntity landscape1 = LandscapeEntity.builder()
                .id(UUID.randomUUID().toString())
                .title("Landscape 1")
                .description("Desc 1")
                .latitude(10.0)
                .longitude(20.0)
                .imageUrl("http://minio/bucket/img1.jpg")
                .status("APPROVED")
                .build();

        LandscapeEntity landscape2 = LandscapeEntity.builder()
                .id(UUID.randomUUID().toString())
                .title("Landscape 2")
                .description("Desc 2")
                .latitude(15.0)
                .longitude(25.0)
                .imageUrl("http://minio/bucket/img2.jpg")
                .status("PENDING")
                .build();

        when(landscapeRepository.findByUserId(123L))
                .thenReturn(Flux.just(landscape1, landscape2));

        Flux<LandscapeDetailResponse> result = appService.getMyLandscapes(
                userId, "http://localhost:8080"
        );

        StepVerifier.create(result)
                .assertNext(response -> {
                    assert response.title().equals("Landscape 1");
                    assert response.status().equals("APPROVED");
                })
                .assertNext(response -> {
                    assert response.title().equals("Landscape 2");
                    assert response.status().equals("PENDING");
                })
                .verifyComplete();
    }

    @Test
    void getMyLandscapesEmpty() {
        when(landscapeRepository.findByUserId(123L))
                .thenReturn(Flux.empty());

        Flux<LandscapeDetailResponse> result = appService.getMyLandscapes(
                "123", "http://localhost:8080"
        );

        StepVerifier.create(result)
                .verifyComplete();
    }
}
