package com.brayanpv.app.service.contracts;

import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Mono;

public interface IS3Service {
    Mono<String> uploadFile(FilePart filePart);

}
