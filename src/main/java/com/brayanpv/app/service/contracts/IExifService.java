package com.brayanpv.app.service.contracts;

import com.brayanpv.app.model.ExifResult;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Mono;

public interface IExifService {

    Mono<ExifResult> extractCoordinates(FilePart filePart);
}
