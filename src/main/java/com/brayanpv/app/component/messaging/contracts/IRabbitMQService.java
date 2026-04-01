package com.brayanpv.app.component.messaging.contracts;

import com.brayanspv.library.model.events.LandscapeEvent;
import reactor.core.publisher.Mono;

public interface IRabbitMQService {
    Mono<Void> publishLandscape(LandscapeEvent event);
}
