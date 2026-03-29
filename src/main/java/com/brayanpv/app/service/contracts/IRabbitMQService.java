package com.brayanpv.app.service.contracts;

import com.brayanspv.library.model.events.LandscapeEvent;
import reactor.core.publisher.Mono;

public interface IRabbitMQService {
    Mono<Void> publishLandscape(LandscapeEvent event);
}
