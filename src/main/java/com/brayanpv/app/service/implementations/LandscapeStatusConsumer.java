package com.brayanpv.app.service.implementations;

import com.brayanpv.app.repositories.contracts.ILandscapeRepository;
import com.brayanpv.app.service.contracts.ILandscapeStatusConsumer;
import com.brayanspv.library.model.events.LandscapeStatusEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Service
@Log4j2
@RequiredArgsConstructor
public class LandscapeStatusConsumer implements ILandscapeStatusConsumer {

    private final ILandscapeRepository landscapeRepository;

    @Override
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
