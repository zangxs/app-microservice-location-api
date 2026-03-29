package com.brayanpv.app.service.implementations;

import com.brayanpv.app.repositories.contracts.ILandscapeLikeRepository;
import com.brayanpv.app.repositories.entities.LandscapeLikeEntity;
import com.brayanpv.app.service.contracts.ILandscapeLikeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Log4j2
public class LandscapeLikeService implements ILandscapeLikeService {

    private final ILandscapeLikeRepository likeRepository;


    @Override
    public Mono<Void> like(String landscapeId, String userId) {
        return likeRepository.findByLandscapeIdAndUserId(
                        UUID.fromString(landscapeId), Long.parseLong(userId))
                .flatMap(existing -> Mono.<Void>error(
                        new RuntimeException("Ya diste like a este paisaje")))
                .switchIfEmpty(Mono.defer(() -> {
                    LandscapeLikeEntity like = LandscapeLikeEntity.builder()
                            .landscapeId(UUID.fromString(landscapeId))
                            .userId(Long.parseLong(userId))
                            .createdAt(LocalDateTime.now(ZoneOffset.UTC))
                            .build();
                    return likeRepository.save(like).then();
                }));
    }

    @Override
    public Mono<Void> unlike(String landscapeId, String userId) {
        return likeRepository.deleteByLandscapeIdAndUserId(
                UUID.fromString(landscapeId), Long.parseLong(userId));
    }

    @Override
    public Mono<Long> countLikes(String landscapeId) {
        return likeRepository.countByLandscapeId(UUID.fromString(landscapeId));
    }
}
