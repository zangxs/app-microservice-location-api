package com.brayanpv.app.service.contracts;

import com.brayanpv.app.model.message.LandscapeEvent;
import reactor.core.publisher.Mono;

public interface IRabbitMQService {
    Mono<Void> publishLandscape(LandscapeEvent event);
}
