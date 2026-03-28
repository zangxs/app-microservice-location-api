package com.brayanpv.app.component.filter;

import com.brayanpv.app.model.response.generic.ApiResponse;
import com.brayanpv.app.service.contracts.IJWTService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Component
@RequiredArgsConstructor
public class JWTAuthFilter implements WebFilter {

    private final IJWTService jwtService;
    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
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

        return chain.filter(exchange)
                .contextWrite(ctx -> ctx
                        .put("userId", userId)
                        .put("email", email)
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
}
