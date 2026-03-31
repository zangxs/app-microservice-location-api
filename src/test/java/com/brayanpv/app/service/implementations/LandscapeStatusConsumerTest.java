package com.brayanpv.app.service.implementations;

import com.brayanpv.app.repositories.contracts.ILandscapeRepository;
import com.brayanspv.library.model.events.LandscapeStatusEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LandscapeStatusConsumerTest {

    @Mock
    private ILandscapeRepository landscapeRepository;

    private LandscapeStatusConsumer consumer;

    @BeforeEach
    void setUp() {
        consumer = new LandscapeStatusConsumer(landscapeRepository);
    }

    @Test
    void consumeSuccess() {
        LandscapeStatusEvent event = mock(LandscapeStatusEvent.class);
        when(event.landscapeId()).thenReturn("landscape-123");
        when(event.status()).thenReturn("APPROVED");

        when(landscapeRepository.updateStatus("APPROVED", "landscape-123"))
                .thenReturn(Mono.empty());

        consumer.consume(event);

        verify(landscapeRepository, times(1)).updateStatus("APPROVED", "landscape-123");
    }

    @Test
    void consumeError() {
        LandscapeStatusEvent event = mock(LandscapeStatusEvent.class);
        when(event.landscapeId()).thenReturn("landscape-456");
        when(event.status()).thenReturn("REJECTED");

        when(landscapeRepository.updateStatus("REJECTED", "landscape-456"))
                .thenReturn(Mono.error(new RuntimeException("DB error")));

        consumer.consume(event);

        verify(landscapeRepository, times(1)).updateStatus("REJECTED", "landscape-456");
    }
}
