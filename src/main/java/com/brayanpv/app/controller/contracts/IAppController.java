package com.brayanpv.app.controller.contracts;

import com.brayanpv.app.model.request.LandscapeRequest;
import com.brayanpv.app.model.response.generic.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.core.io.buffer.DataBuffer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IAppController {

    Mono<ResponseEntity<ApiResponse>> uploadLocation(
            @RequestPart("file") FilePart file,
            @RequestPart("title") String title,
            @RequestPart(value = "description", required = false) String description);

    Mono<ResponseEntity<ApiResponse>> getNearby(
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lng,
            @RequestParam(required = false, defaultValue = "50") Integer radius,
            ServerHttpRequest httpRequest);

    Mono<ResponseEntity<ApiResponse>> like(
            @PathVariable String id,
            ServerWebExchange exchange);

    Mono<ResponseEntity<ApiResponse>> unlike(
            @PathVariable String id,
            ServerWebExchange exchange);

    Mono<ResponseEntity<ApiResponse>> countLikes(@PathVariable String id);

    Flux<DataBuffer> serveImage(@PathVariable String filename);

    Mono<ResponseEntity<ApiResponse>> getLandscape(@PathVariable String id,
                                                   ServerHttpRequest httpRequest);

    Mono<ResponseEntity<ApiResponse>> hasLiked(@PathVariable String id);

    Mono<ResponseEntity<ApiResponse>> getMyLandscapes(ServerHttpRequest httpRequest);
}
