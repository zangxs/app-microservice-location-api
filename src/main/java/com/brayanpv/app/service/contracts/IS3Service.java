package com.brayanpv.app.service.contracts;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IS3Service {
    Mono<String> uploadFile(FilePart filePart, byte[] bytes);
    Flux<DataBuffer> getFileStream(String filename);
}
