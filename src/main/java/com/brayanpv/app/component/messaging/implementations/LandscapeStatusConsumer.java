package com.brayanpv.app.component.messaging.implementations;

import com.brayanpv.app.component.messaging.contracts.ILandscapeStatusConsumer;
import com.brayanpv.app.repositories.contracts.ILandscapeRepository;
import com.brayanspv.library.model.events.LandscapeStatusEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Log4j2
@RequiredArgsConstructor
public class LandscapeStatusConsumer implements ILandscapeStatusConsumer {

    private final ILandscapeRepository landscapeRepository;

    @Override
    @RabbitListener(queues = "${app.rabbitmq.status-queue}")
    public void consume(LandscapeStatusEvent event) {
        log.info("Status event received: landscapeId={}, status={}",
                event.landscapeId(), event.status());

        landscapeRepository.updateStatus(event.status(), event.landscapeId())
                .subscribe(
                        null,
                        error -> log.error("Error updating status: {}", error.getMessage()),
                        () -> log.info("Status updated successfully: {}", event.landscapeId())
                );
    }
}
