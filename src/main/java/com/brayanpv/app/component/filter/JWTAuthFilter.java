package com.brayanpv.app.component.filter;

import com.brayanpv.app.model.response.generic.ApiResponse;
import com.brayanpv.app.service.contracts.IJWTService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Component
@RequiredArgsConstructor
@Log4j2
public class JWTAuthFilter implements WebFilter {

    private final IJWTService jwtService;
    private final ObjectMapper objectMapper;


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        if (exchange.getRequest().getMethod() == HttpMethod.OPTIONS) {
            return chain.filter(exchange);
        }

        String path = exchange.getRequest().getPath().value();

        if (isPublicPath(path)) {
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return unauthorized(exchange, "Token no proporcionado");

        }
        String token = authHeader.substring(7);

        if (!jwtService.validateToken(token)) {
            return unauthorized(exchange, "Token inválido o expirado");
        }

        String userId = jwtService.extractUserId(token);
        String email = jwtService.extractEmail(token);
        String username = jwtService.extractField(token, "username");

        return chain.filter(exchange)
                .contextWrite(ctx -> ctx
                        .put("userId", userId)
                        .put("email", email)
                        .put("username", username)
                );
    }


    private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
        try {
            ApiResponse response = ApiResponse.builder()
                    .dateTime(LocalDateTime.now(ZoneOffset.UTC))
                    .code(HttpStatus.UNAUTHORIZED.value())
                    .data(message)
                    .build();

            byte[] bytes = objectMapper.writeValueAsBytes(response);

            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

            DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
            return exchange.getResponse().writeWith(Mono.just(buffer));

        } catch (JsonProcessingException e) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }

    // Para paths con wildcards usa startsWith o un patrón
    private boolean isPublicPath(String path) {
        return path.equals("/app-microservice-location/landscapes/nearby")
                || path.matches("/app-microservice-location/landscapes/.+/likes")
                || path.startsWith("/app-microservice-location/images/")
                || path.matches("(?i)/app-microservice-location/landscapes/[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}");

    }
}
