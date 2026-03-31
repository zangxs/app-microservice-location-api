package com.brayanpv.app.service.implementations;

import com.brayanpv.app.model.ExifResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.nio.file.Files;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExifServiceTest {

    private final ExifService exifService = new ExifService();

    @Test
    void extractCoordinatesWithGpsData() throws IOException {
        byte[] imageBytes = Files.readAllBytes(
                new ClassPathResource("test-with-gps.jpeg").getFile().toPath());

        FilePart mockFilePart = mock(FilePart.class);
        DataBuffer dataBuffer = DefaultDataBufferFactory.sharedInstance.wrap(imageBytes);
        when(mockFilePart.content()).thenReturn(Flux.just(dataBuffer));

        Mono<ExifResult> result = exifService.extractCoordinates(mockFilePart);

        StepVerifier.create(result)
                .assertNext(exifResult -> {
                    assert exifResult.bytes().length == imageBytes.length;
                    assert exifResult.latitude() != null;
                    assert exifResult.longitude() != null;
                })
                .verifyComplete();
    }

    @Test
    void extractCoordinatesNoGpsData() throws IOException {
        byte[] imageBytes = Files.readAllBytes(
                new ClassPathResource("test-no-gps.jpg").getFile().toPath());

        FilePart mockFilePart = mock(FilePart.class);
        DataBuffer dataBuffer = DefaultDataBufferFactory.sharedInstance.wrap(imageBytes);
        when(mockFilePart.content()).thenReturn(Flux.just(dataBuffer));

        Mono<ExifResult> result = exifService.extractCoordinates(mockFilePart);

        StepVerifier.create(result)
                .assertNext(exifResult -> {
                    assert exifResult.bytes().length == imageBytes.length;
                    assert exifResult.latitude() == null;
                    assert exifResult.longitude() == null;
                })
                .verifyComplete();
    }
}
