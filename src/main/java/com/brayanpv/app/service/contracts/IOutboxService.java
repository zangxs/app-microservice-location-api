package com.brayanpv.app.service.contracts;

import com.brayanpv.app.repositories.entities.LandscapeEntity;
import reactor.core.publisher.Mono;

public interface IOutboxService {
    Mono<Void> publishLandscapeCreated(LandscapeEntity landscape);
}
