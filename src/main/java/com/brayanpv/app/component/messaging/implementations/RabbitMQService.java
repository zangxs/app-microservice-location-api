package com.brayanpv.app.component.messaging.implementations;

import com.brayanpv.app.component.messaging.contracts.IRabbitMQService;
import com.brayanspv.library.model.events.LandscapeEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Component
@Log4j2
@RequiredArgsConstructor
public class RabbitMQService implements IRabbitMQService {

    private final RabbitTemplate rabbitTemplate;

    @Value("${app.rabbitmq.exchange}")
    private String exchange;

    @Value("${app.rabbitmq.routing-key}")
    private String routingKey;

    @Override
    public Mono<Void> publishLandscape(LandscapeEvent event) {
        return Mono.fromCallable(() -> {
                    rabbitTemplate.convertAndSend(exchange, routingKey, event);
                    log.info("Event published to RabbitMQ: {}", event.landscapeId());
                    return null;
                })
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }
}
