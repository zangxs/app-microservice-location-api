package com.brayanpv.app.component.configuration;

import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.WebFilter;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@Configuration
@Log4j2
public class CorsConfig {

    @Bean
    public WebFilter corsFilter() {
        return (exchange, chain) -> {
            log.info("CorsFilter Called");
            ServerHttpResponse response = exchange.getResponse();
            HttpHeaders headers = response.getHeaders();
            log.info("CorsFilter Headers: " + headers);
            // Obtén el origen de la petición
            String origin = exchange.getRequest().getHeaders().getOrigin();

            // Lista de orígenes permitidos
            List<String> allowedOrigins = Arrays.asList(
                    "http://localhost:5173",
                    "http://brayan-b550m-ds3h-ac.tailbfab80.ts.net:5173",
                    "http://100.105.171.120:5173"
            );

            // Si el origen está permitido, agrega el header
            if (origin != null && allowedOrigins.contains(origin)) {
                headers.add("Access-Control-Allow-Origin", origin);
            }
            headers.add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            headers.add("Access-Control-Allow-Headers", "Authorization, Content-Type");
            headers.add("Access-Control-Max-Age", "3600");

            if (exchange.getRequest().getMethod() == HttpMethod.OPTIONS) {
                response.setStatusCode(HttpStatus.OK);
                return Mono.empty();
            }

            return chain.filter(exchange);
        };
    }
}