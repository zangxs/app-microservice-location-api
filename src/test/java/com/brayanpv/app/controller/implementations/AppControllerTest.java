package com.brayanpv.app.controller.implementations;

import com.brayanpv.app.model.response.LandscapeDetailResponse;
import com.brayanpv.app.model.response.LandscapeResponse;
import com.brayanpv.app.model.response.NearbyLandscapeResponse;
import com.brayanpv.app.model.response.generic.ApiResponse;
import com.brayanpv.app.service.contracts.IAppService;
import com.brayanpv.app.service.contracts.ILandscapeLikeService;
import com.brayanpv.app.service.contracts.IS3Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.net.URI;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AppControllerTest {

    @Mock
    private IAppService appService;

    @Mock
    private ILandscapeLikeService likeService;

    @Mock
    private IS3Service s3Service;

    @InjectMocks
    private AppController appController;

    private WebTestClient webTestClient;

    @BeforeEach
    public void setUp() {
        this.webTestClient = WebTestClient.bindToController(appController).build();
    }

    @Test
    void uploadLocationOk() {
        LandscapeResponse expected = new LandscapeResponse("landscape-123", "UPLOADED");

        FilePart mockFilePart = new MockFilePart("test.jpg");
        when(appService.uploadFile(any())).thenReturn(Mono.just(expected));

        Mono<ResponseEntity<ApiResponse>> result = appController.uploadLocation(
                mockFilePart,
                "Test Title",
                "Test Description",
                "10.0",
                "20.0"
        );

        StepVerifier.create(result)
                .assertNext(response -> {
                    assert response.getStatusCode().is2xxSuccessful();
                    assert response.getBody() != null;
                    assert response.getBody().getCode() == 200;
                    LandscapeResponse body = (LandscapeResponse) response.getBody().getData();
                    assert body.getId().equals("landscape-123");
                    assert body.getStatus().equals("UPLOADED");
                })
                .verifyComplete();

        verify(appService, times(1)).uploadFile(any());
    }

    @Test
    void getNearbyWithParams() {
        NearbyLandscapeResponse landscape = new NearbyLandscapeResponse(
                "1", "Test", "Desc", 10.0, 20.0, "http://img.jpg", 1.5
        );

        when(appService.getNearby(eq(10.0), eq(20.0), eq(25), anyString(), anyString()))
                .thenReturn(Flux.just(landscape));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/landscapes/nearby")
                        .queryParam("lat", 10.0)
                        .queryParam("lng", 20.0)
                        .queryParam("radius", 25)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.code").isEqualTo(200)
                .jsonPath("$.data").isArray()
                .jsonPath("$.data[0].id").isEqualTo("1")
                .jsonPath("$.data[0].title").isEqualTo("Test")
                .jsonPath("$.data[0].distance").isEqualTo(1.5);

        verify(appService, times(1)).getNearby(eq(10.0), eq(20.0), eq(25), anyString(), anyString());
    }

    @Test
    void getNearbyDefaultRadius() {
        when(appService.getNearby(isNull(), isNull(), eq(50), anyString(), anyString()))
                .thenReturn(Flux.empty());

        webTestClient.get()
                .uri("/landscapes/nearby")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.code").isEqualTo(200)
                .jsonPath("$.data").isArray()
                .jsonPath("$.data").isEmpty();

        verify(appService, times(1)).getNearby(isNull(), isNull(), eq(50), anyString(), anyString());
    }

    @Test
    void likeOk() {
        String landscapeId = "landscape-123";

        when(likeService.like(eq(landscapeId), anyString())).thenReturn(Mono.empty());

        Mono<ResponseEntity<ApiResponse>> result = appController.like(landscapeId, null)
                .contextWrite(ctx -> ctx.put("userId", "user-123"));

        StepVerifier.create(result)
                .assertNext(response -> {
                    assert response.getStatusCode().is2xxSuccessful();
                    assert response.getBody() != null;
                    assert response.getBody().getCode() == 200;
                    assert response.getBody().getData().equals("Like agregado");
                })
                .verifyComplete();

        verify(likeService, times(1)).like(eq(landscapeId), eq("user-123"));
    }

    @Test
    void unlikeOk() {
        String landscapeId = "landscape-123";

        when(likeService.unlike(eq(landscapeId), anyString())).thenReturn(Mono.empty());

        Mono<ResponseEntity<ApiResponse>> result = appController.unlike(landscapeId, null)
                .contextWrite(ctx -> ctx.put("userId", "user-123"));

        StepVerifier.create(result)
                .assertNext(response -> {
                    assert response.getStatusCode().is2xxSuccessful();
                    assert response.getBody() != null;
                    assert response.getBody().getCode() == 200;
                    assert response.getBody().getData().equals("Like eliminado");
                })
                .verifyComplete();

        verify(likeService, times(1)).unlike(eq(landscapeId), eq("user-123"));
    }

    @Test
    void countLikesOk() {
        String landscapeId = "landscape-123";

        when(likeService.countLikes(landscapeId)).thenReturn(Mono.just(42L));

        webTestClient.get()
                .uri("/landscapes/{id}/likes", landscapeId)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.code").isEqualTo(200)
                .jsonPath("$.data").isEqualTo(42);

        verify(likeService, times(1)).countLikes(landscapeId);
    }

    @Test
    void serveImageOk() {
        String filename = "image-123.jpg";
        byte[] imageBytes = new byte[]{1, 2, 3, 4};

        when(s3Service.getFile(filename)).thenReturn(Mono.just(imageBytes));

        webTestClient.get()
                .uri("/images/{filename}", filename)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("image/jpeg")
                .expectHeader().exists("Access-Control-Allow-Origin")
                .expectHeader().exists("Cache-Control")
                .expectBody(byte[].class)
                .isEqualTo(imageBytes);

        verify(s3Service, times(1)).getFile(filename);
    }

    @Test
    void getLandscapeOk() {
        String landscapeId = "landscape-123";
        LandscapeDetailResponse detail = new LandscapeDetailResponse(
                landscapeId, "Test Title", "Test Desc", 10.0, 20.0, "http://img.jpg", "ACTIVE"
        );

        when(appService.getLandscape(eq(landscapeId), anyString())).thenReturn(Mono.just(detail));

        webTestClient.get()
                .uri("/landscapes/{id}", landscapeId)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.code").isEqualTo(200)
                .jsonPath("$.data.id").isEqualTo(landscapeId)
                .jsonPath("$.data.title").isEqualTo("Test Title")
                .jsonPath("$.data.latitude").isEqualTo(10.0)
                .jsonPath("$.data.longitude").isEqualTo(20.0);

        verify(appService, times(1)).getLandscape(eq(landscapeId), anyString());
    }

    @Test
    void getLandscapeNotFound() {
        String landscapeId = "non-existent";

        when(appService.getLandscape(eq(landscapeId), anyString())).thenReturn(Mono.empty());

        webTestClient.get()
                .uri("/landscapes/{id}", landscapeId)
                .exchange()
                .expectStatus().isNotFound();

        verify(appService, times(1)).getLandscape(eq(landscapeId), anyString());
    }

    @Test
    void hasLikedOk() {
        String landscapeId = "landscape-123";

        when(appService.hasLiked(eq(landscapeId), anyString())).thenReturn(Mono.just(true));

        Mono<ResponseEntity<ApiResponse>> result = appController.hasLiked(landscapeId)
                .contextWrite(ctx -> ctx.put("userId", "user-123"));

        StepVerifier.create(result)
                .assertNext(response -> {
                    assert response.getStatusCode().is2xxSuccessful();
                    assert response.getBody() != null;
                    assert response.getBody().getCode() == 200;
                    assert Boolean.TRUE.equals(response.getBody().getData());
                })
                .verifyComplete();

        verify(appService, times(1)).hasLiked(eq(landscapeId), eq("user-123"));
    }

    @Test
    void hasLikedFalse() {
        String landscapeId = "landscape-123";

        when(appService.hasLiked(eq(landscapeId), anyString())).thenReturn(Mono.just(false));

        Mono<ResponseEntity<ApiResponse>> result = appController.hasLiked(landscapeId)
                .contextWrite(ctx -> ctx.put("userId", "user-123"));

        StepVerifier.create(result)
                .assertNext(response -> {
                    assert response.getStatusCode().is2xxSuccessful();
                    assert response.getBody() != null;
                    assert response.getBody().getCode() == 200;
                    assert Boolean.FALSE.equals(response.getBody().getData());
                })
                .verifyComplete();

        verify(appService, times(1)).hasLiked(eq(landscapeId), eq("user-123"));
    }

    @Test
    void getMyLandscapesOk() throws java.net.URISyntaxException {
        LandscapeDetailResponse landscape1 = new LandscapeDetailResponse(
                "1", "My Landscape 1", "Desc 1", 10.0, 20.0, "http://img1.jpg", "ACTIVE"
        );
        LandscapeDetailResponse landscape2 = new LandscapeDetailResponse(
                "2", "My Landscape 2", "Desc 2", 15.0, 25.0, "http://img2.jpg", "ACTIVE"
        );

        when(appService.getMyLandscapes(anyString(), anyString()))
                .thenReturn(Flux.just(landscape1, landscape2));

        ServerHttpRequest mockRequest = mock(ServerHttpRequest.class);
        when(mockRequest.getURI()).thenReturn(new URI("http://localhost:8080"));

        Mono<ResponseEntity<ApiResponse>> result = appController.getMyLandscapes(mockRequest)
                .contextWrite(ctx -> ctx.put("userId", "user-123"));

        StepVerifier.create(result)
                .assertNext(response -> {
                    assert response.getStatusCode().is2xxSuccessful();
                    assert response.getBody() != null;
                    assert response.getBody().getCode() == 200;
                    java.util.List<?> landscapes = (java.util.List<?>) response.getBody().getData();
                    assert landscapes.size() == 2;
                    LandscapeDetailResponse first = (LandscapeDetailResponse) landscapes.get(0);
                    assert first.id().equals("1");
                    assert first.title().equals("My Landscape 1");
                })
                .verifyComplete();

        verify(appService, times(1)).getMyLandscapes(eq("user-123"), anyString());
    }

    @Test
    void getMyLandscapesEmpty() throws java.net.URISyntaxException {
        when(appService.getMyLandscapes(anyString(), anyString()))
                .thenReturn(Flux.empty());

        ServerHttpRequest mockRequest = mock(ServerHttpRequest.class);
        when(mockRequest.getURI()).thenReturn(new URI("http://localhost:8080"));

        Mono<ResponseEntity<ApiResponse>> result = appController.getMyLandscapes(mockRequest)
                .contextWrite(ctx -> ctx.put("userId", "user-123"));

        StepVerifier.create(result)
                .assertNext(response -> {
                    assert response.getStatusCode().is2xxSuccessful();
                    assert response.getBody() != null;
                    assert response.getBody().getCode() == 200;
                    java.util.List<?> landscapes = (java.util.List<?>) response.getBody().getData();
                    assert landscapes.isEmpty();
                })
                .verifyComplete();

        verify(appService, times(1)).getMyLandscapes(eq("user-123"), anyString());
    }

    static class MockFilePart implements FilePart {
        private final String filename;

        MockFilePart(String filename) {
            this.filename = filename;
        }

        @Override
        public String name() {
            return "file";
        }

        @Override
        public String filename() {
            return filename;
        }

        @Override
        public Flux<DataBuffer> content() {
            return Flux.empty();
        }

        @Override
        public Mono<Void> transferTo(java.nio.file.Path dest) {
            return Mono.empty();
        }

        @Override
        public Mono<Void> delete() {
            return Mono.empty();
        }

        @Override
        public org.springframework.http.HttpHeaders headers() {
            return new org.springframework.http.HttpHeaders();
        }
    }
}
