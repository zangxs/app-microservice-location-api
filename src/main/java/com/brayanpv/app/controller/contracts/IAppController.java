package com.brayanpv.app.controller.contracts;

import com.brayanpv.app.model.request.LandscapeRequest;
import com.brayanpv.app.model.response.generic.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.RequestPart;
import reactor.core.publisher.Mono;

public interface IAppController {

    Mono<ResponseEntity<ApiResponse>> uploadLocation(
            @RequestPart("file") FilePart file,
            @RequestPart("title") String title,
            @RequestPart("description") String description,
            @RequestPart("latitude") String latitude,
            @RequestPart("longitude") String longitude);
}
