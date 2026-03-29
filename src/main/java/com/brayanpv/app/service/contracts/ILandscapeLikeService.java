package com.brayanpv.app.service.contracts;

import reactor.core.publisher.Mono;

public interface ILandscapeLikeService {
    Mono<Void> like(String landscapeId, String userId);
    Mono<Void> unlike(String landscapeId, String userId);
    Mono<Long> countLikes(String landscapeId);
}
