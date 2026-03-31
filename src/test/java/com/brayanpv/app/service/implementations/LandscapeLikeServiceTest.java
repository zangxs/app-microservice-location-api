package com.brayanpv.app.service.implementations;

import com.brayanpv.app.repositories.contracts.ILandscapeLikeRepository;
import com.brayanpv.app.repositories.entities.LandscapeLikeEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LandscapeLikeServiceTest {

    @Mock
    private ILandscapeLikeRepository likeRepository;

    private LandscapeLikeService likeService;

    @BeforeEach
    void setUp() {
        likeService = new LandscapeLikeService(likeRepository);
    }

    @Test
    void likeSuccess() {
        String landscapeId = UUID.randomUUID().toString();
        String userId = "123";

        when(likeRepository.findByLandscapeIdAndUserId(any(UUID.class), eq(123L)))
                .thenReturn(Mono.empty());
        when(likeRepository.save(any(LandscapeLikeEntity.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        Mono<Void> result = likeService.like(landscapeId, userId);

        StepVerifier.create(result)
                .verifyComplete();

        verify(likeRepository, times(1)).findByLandscapeIdAndUserId(any(UUID.class), eq(123L));
        verify(likeRepository, times(1)).save(any(LandscapeLikeEntity.class));
    }

    @Test
    void likeAlreadyExists() {
        String landscapeId = UUID.randomUUID().toString();
        String userId = "123";

        LandscapeLikeEntity existing = LandscapeLikeEntity.builder()
                .landscapeId(UUID.fromString(landscapeId))
                .userId(123L)
                .createdAt(LocalDateTime.now())
                .build();

        when(likeRepository.findByLandscapeIdAndUserId(any(UUID.class), eq(123L)))
                .thenReturn(Mono.just(existing));

        Mono<Void> result = likeService.like(landscapeId, userId);

        StepVerifier.create(result)
                .expectErrorMessage("Ya diste like a este paisaje")
                .verify();

        verify(likeRepository, times(1)).findByLandscapeIdAndUserId(any(UUID.class), eq(123L));
        verify(likeRepository, never()).save(any());
    }

    @Test
    void unlikeSuccess() {
        String landscapeId = UUID.randomUUID().toString();
        String userId = "456";

        when(likeRepository.deleteByLandscapeIdAndUserId(any(UUID.class), eq(456L)))
                .thenReturn(Mono.empty());

        Mono<Void> result = likeService.unlike(landscapeId, userId);

        StepVerifier.create(result)
                .verifyComplete();

        verify(likeRepository, times(1)).deleteByLandscapeIdAndUserId(any(UUID.class), eq(456L));
    }

    @Test
    void countLikesSuccess() {
        String landscapeId = UUID.randomUUID().toString();

        when(likeRepository.countByLandscapeId(any(UUID.class)))
                .thenReturn(Mono.just(10L));

        Mono<Long> result = likeService.countLikes(landscapeId);

        StepVerifier.create(result)
                .expectNext(10L)
                .verifyComplete();

        verify(likeRepository, times(1)).countByLandscapeId(any(UUID.class));
    }
}
