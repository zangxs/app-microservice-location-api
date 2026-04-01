package com.brayanpv.app.component.messaging.producer;

import com.brayanpv.app.component.messaging.implementations.RabbitMQService;
import com.brayanspv.library.model.events.LandscapeEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RabbitMQServiceTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    private RabbitMQService rabbitMQService;

    @BeforeEach
    void setUp() {
        rabbitMQService = new RabbitMQService(rabbitTemplate);
        ReflectionTestUtils.setField(rabbitMQService, "exchange", "test-exchange");
        ReflectionTestUtils.setField(rabbitMQService, "routingKey", "test-routing-key");
    }

    @Test
    void publishLandscapeSuccess() {
        LandscapeEvent event = mock(LandscapeEvent.class);
        when(event.landscapeId()).thenReturn("landscape-123");

        doNothing().when(rabbitTemplate).convertAndSend(anyString(), anyString(), (Object) any());

        Mono<Void> result = rabbitMQService.publishLandscape(event);

        StepVerifier.create(result)
                .verifyComplete();

        verify(rabbitTemplate, times(1)).convertAndSend(
                eq("test-exchange"),
                eq("test-routing-key"),
                (Object) eq(event)
        );
    }

    @Test
    void publishLandscapeError() {
        LandscapeEvent event = mock(LandscapeEvent.class);

        doThrow(new RuntimeException("RabbitMQ connection failed"))
                .when(rabbitTemplate).convertAndSend(anyString(), anyString(), (Object) any());

        Mono<Void> result = rabbitMQService.publishLandscape(event);

        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();
    }
}
