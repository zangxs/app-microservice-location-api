package com.brayanpv.app.controller.implementations;

import com.brayanpv.app.controller.contracts.IAppController;
import com.brayanpv.app.model.request.LandscapeRequest;
import com.brayanpv.app.model.response.generic.ApiResponse;
import com.brayanpv.app.service.contracts.IAppService;
import com.brayanpv.app.service.contracts.ILandscapeLikeService;
import com.brayanpv.app.service.contracts.IS3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@RestController
@Log4j2
@RequiredArgsConstructor
public class AppController implements IAppController {

    private final IAppService appService;
    private final ILandscapeLikeService likeService;
    private final IS3Service s3Service;

    @Override
    @PostMapping(path = "upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<ApiResponse>> uploadLocation(
            @RequestPart("file") FilePart file,
            @RequestPart("title") String title,
            @RequestPart("description") String description,
            @RequestPart("latitude") String latitude,
            @RequestPart("longitude") String longitude
    ) {
        log.info("request received: title={}, lat={}, lng={}", title, latitude, longitude);

        LandscapeRequest request = new LandscapeRequest(
                file,
                title,
                description,
                Double.parseDouble(latitude),
                Double.parseDouble(longitude)
        );

        log.info("request received: request={}", request);

        return appService.uploadFile(request)
                .map(locationResponse -> ResponseEntity.ok(ApiResponse.builder()
                        .dateTime(LocalDateTime.now(ZoneOffset.UTC))
                        .code(200)
                        .data(locationResponse).build())
                )
                .onErrorResume(Mono::error);
    }

    @Override
    @GetMapping("/landscapes/nearby")
    public Mono<ResponseEntity<ApiResponse>> getNearby(@RequestParam(required = false) Double lat,
                                                       @RequestParam(required = false) Double lng,
                                                       @RequestParam(required = false, defaultValue = "50") Integer radius,
                                                       ServerHttpRequest httpRequest) {
        String ip = httpRequest.getRemoteAddress() != null
                ? httpRequest.getRemoteAddress().getAddress().getHostAddress()
                : "190.85.100.1";

        // Extrae el baseUrl del request: scheme + host + port
        String baseUrl = httpRequest.getURI().getScheme() + "://" + httpRequest.getURI().getAuthority();

        return appService.getNearby(lat, lng, radius, ip, baseUrl)
                .collectList()
                .map(landscapes -> ResponseEntity.ok(ApiResponse.builder()
                        .dateTime(LocalDateTime.now(ZoneOffset.UTC))
                        .code(200)
                        .data(landscapes)
                        .build()));
    }

    @Override
    @PostMapping("/landscapes/{id}/like")
    public Mono<ResponseEntity<ApiResponse>> like(@PathVariable String id, ServerWebExchange exchange) {
        log.info("request received: id={}", id);
        return Mono.deferContextual(ctx -> {
                    String userId = ctx.get("userId");
                    log.info("userId={}", userId);
                    return likeService.like(id, userId)
                            .thenReturn(ResponseEntity.ok(ApiResponse.builder()
                                    .dateTime(LocalDateTime.now(ZoneOffset.UTC))
                                    .code(200)
                                    .data("Like agregado")
                                    .build()));
                });
    }

    @Override
    @DeleteMapping("/landscapes/{id}/like")
    public Mono<ResponseEntity<ApiResponse>> unlike(@PathVariable String id, ServerWebExchange exchange) {
        log.info("request received: id={}", id);
        return Mono.deferContextual(ctx -> {
            String userId = ctx.get("userId");
            return likeService.unlike(id, userId)
                    .thenReturn(ResponseEntity.ok(ApiResponse.builder()
                            .dateTime(LocalDateTime.now(ZoneOffset.UTC))
                            .code(200)
                            .data("Like eliminado")
                            .build()));
        });
    }

    @Override
    @GetMapping("/landscapes/{id}/likes")
    public Mono<ResponseEntity<ApiResponse>> countLikes(@PathVariable String id) {
        log.info("request received: id={}", id);

        return likeService.countLikes(id)
                .map(count -> ResponseEntity.ok(ApiResponse.builder()
                        .dateTime(LocalDateTime.now(ZoneOffset.UTC))
                        .code(200)
                        .data(count)
                        .build()));
    }

    @Override
    @GetMapping("/images/{filename}")
    public Mono<ResponseEntity<byte[]>> serveImage(
            @PathVariable String filename) {
        return s3Service.getFile(filename)
                .map(bytes -> ResponseEntity.ok()
                        .header("Content-Type", "image/jpeg")
                        .header("Access-Control-Allow-Origin", "*")
                        .header("Cache-Control", "public, max-age=86400")
                        .body(bytes));
    }

    @Override
    @GetMapping("/landscapes/{id}")
    public Mono<ResponseEntity<ApiResponse>> getLandscape(@PathVariable String id, ServerHttpRequest httpRequest) {
        log.info("request received getLandscape: id={}", id);
        String baseUrl = httpRequest.getURI().getScheme() + "://" + httpRequest.getURI().getAuthority();

        return appService.getLandscape(id, baseUrl)
                .map(landscape -> ResponseEntity.ok(ApiResponse.builder()
                        .dateTime(LocalDateTime.now(ZoneOffset.UTC))
                        .code(200)
                        .data(landscape)
                        .build()))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Override
    @GetMapping("/landscapes/{id}/liked")
    public Mono<ResponseEntity<ApiResponse>> hasLiked(@PathVariable String id) {
        return Mono.deferContextual(ctx -> {
            String userId = ctx.get("userId");
            return appService.hasLiked(id, userId)
                    .map(liked -> ResponseEntity.ok(ApiResponse.builder()
                            .dateTime(LocalDateTime.now(ZoneOffset.UTC))
                            .code(200)
                            .data(liked)
                            .build()));
        });
    }

    @Override
    @GetMapping("/landscapes/my")
    public Mono<ResponseEntity<ApiResponse>> getMyLandscapes(ServerHttpRequest httpRequest) {
        return Mono.deferContextual(ctx -> {
            String userId = ctx.get("userId");
            String baseUrl = httpRequest.getURI().getScheme() + "://" + httpRequest.getURI().getAuthority();
            return appService.getMyLandscapes(userId, baseUrl)
                    .collectList()
                    .map(landscapes -> ResponseEntity.ok(ApiResponse.builder()
                            .dateTime(LocalDateTime.now(ZoneOffset.UTC))
                            .code(200)
                            .data(landscapes)
                            .build()));
        });
    }
}
