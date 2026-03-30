package com.brayanpv.app.service.implementations;

import com.brayanpv.app.service.contracts.IS3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class S3Service implements IS3Service {
    private final S3Client s3Client;

    @Value("${minio.bucket}")
    private String bucket;

    @Value("${minio.url}")
    private String minioUrl;

    @Value("${app.producer-url}")
    private String producerUrl;

    @Override
    public Mono<String> uploadFile(FilePart filePart, byte[] bytes) {
        String fileName = UUID.randomUUID() + "-" + filePart.filename();

        return Mono.fromCallable(() -> {
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucket)
                            .key(fileName)
                            .contentType(filePart.headers().getContentType() != null
                                    ? filePart.headers().getContentType().toString()
                                    : "image/jpeg")
                            .build(),
                    RequestBody.fromBytes(bytes)
            );
            return minioUrl + "/" + bucket + "/" + fileName;
        }).subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<byte[]> getFile(String filename) {
        return Mono.fromCallable(() -> {
            GetObjectRequest request = GetObjectRequest.builder()
                    .bucket(bucket)
                    .key(filename)
                    .build();
            ResponseInputStream<GetObjectResponse> response = s3Client.getObject(request);
            return response.readAllBytes();
        }).subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Flux<DataBuffer> getFileStream(String filename) {
        return Mono.fromCallable(() -> {
                    GetObjectRequest request = GetObjectRequest.builder()
                            .bucket(bucket)
                            .key(filename)
                            .build();
                    return s3Client.getObject(request);
                })
                .subscribeOn(Schedulers.boundedElastic())
                .flatMapMany(inputStream -> DataBufferUtils.readInputStream(
                        () -> inputStream,
                        new DefaultDataBufferFactory(),
                        8192
                ));
    }
}
