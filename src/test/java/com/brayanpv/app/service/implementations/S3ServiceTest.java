package com.brayanpv.app.service.implementations;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.ByteArrayInputStream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class S3ServiceTest {

    @Mock
    private S3Client s3Client;

    private S3Service s3Service;

    @BeforeEach
    void setUp() {
        s3Service = new S3Service(s3Client);
        ReflectionTestUtils.setField(s3Service, "bucket", "test-bucket");
        ReflectionTestUtils.setField(s3Service, "minioUrl", "http://localhost:9000");
    }

    @Test
    void uploadFileWithContentType() {
        FilePart mockFilePart = mock(FilePart.class);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);

        when(mockFilePart.filename()).thenReturn("test.jpg");
        when(mockFilePart.headers()).thenReturn(headers);

        byte[] bytes = new byte[]{1, 2, 3, 4};

        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenReturn(null);

        Mono<String> result = s3Service.uploadFile(mockFilePart, bytes);

        StepVerifier.create(result)
                .assertNext(url -> {
                    assert url.startsWith("http://localhost:9000/test-bucket/");
                    assert url.endsWith("-test.jpg");
                })
                .verifyComplete();

        verify(s3Client, times(1)).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    @Test
    void uploadFileWithoutContentType() {
        FilePart mockFilePart = mock(FilePart.class);
        HttpHeaders headers = new HttpHeaders();

        when(mockFilePart.filename()).thenReturn("photo.png");
        when(mockFilePart.headers()).thenReturn(headers);

        byte[] bytes = new byte[]{5, 6, 7, 8};

        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenReturn(null);

        Mono<String> result = s3Service.uploadFile(mockFilePart, bytes);

        StepVerifier.create(result)
                .assertNext(url -> {
                    assert url.startsWith("http://localhost:9000/test-bucket/");
                    assert url.endsWith("-photo.png");
                })
                .verifyComplete();

        verify(s3Client, times(1)).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    @Test
    void uploadFileError() {
        FilePart mockFilePart = mock(FilePart.class);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);

        when(mockFilePart.filename()).thenReturn("fail.jpg");
        when(mockFilePart.headers()).thenReturn(headers);

        byte[] bytes = new byte[]{1, 2, 3};

        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenThrow(new RuntimeException("S3 connection failed"));

        Mono<String> result = s3Service.uploadFile(mockFilePart, bytes);

        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();
    }

    @SuppressWarnings("unchecked")
    @Test
    void getFileStreamSuccess() throws Exception {
        byte[] fileContent = new byte[]{10, 20, 30, 40, 50};
        ByteArrayInputStream inputStream = new ByteArrayInputStream(fileContent);

        ResponseInputStream<GetObjectResponse> responseStream = mock(ResponseInputStream.class);
        doAnswer(invocation -> {
            byte[] buffer = invocation.getArgument(0);
            int offset = invocation.getArgument(1);
            int length = invocation.getArgument(2);
            return inputStream.read(buffer, offset, length);
        }).when(responseStream).read(any(byte[].class), anyInt(), anyInt());

        when(s3Client.getObject(any(GetObjectRequest.class))).thenReturn(responseStream);

        Flux<DataBuffer> result = s3Service.getFileStream("test-file.jpg");

        StepVerifier.create(result)
                .consumeNextWith(buffer -> {
                    byte[] bytes = new byte[buffer.readableByteCount()];
                    buffer.read(bytes);
                    DataBufferUtils.release(buffer);
                    assert bytes.length == 5;
                    assert bytes[0] == 10;
                    assert bytes[4] == 50;
                })
                .verifyComplete();

        verify(s3Client, times(1)).getObject(any(GetObjectRequest.class));
    }

    @Test
    void getFileStreamError() {
        when(s3Client.getObject(any(GetObjectRequest.class)))
                .thenThrow(new RuntimeException("File not found"));

        Flux<DataBuffer> result = s3Service.getFileStream("non-existent.jpg");

        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();
    }
}
