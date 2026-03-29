package com.brayanpv.app.service.implementations;

import com.brayanpv.app.service.contracts.IIpService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@Log4j2
@RequiredArgsConstructor
public class IpService implements IIpService {

    private final WebClient webClient;

    public IpService() {
        this.webClient = WebClient.builder()
                .baseUrl("http://ip-api.com")
                .build();
    }

    @Override
    public Mono<double[]> getCoordinates(String ip) {
        // Si es IP local usar coordenadas por defecto
        if (ip.equals("127.0.0.1") || ip.equals("0:0:0:0:0:0:0:1") || ip.startsWith("192.168")) {
            return Mono.just(new double[]{4.5709, -74.2973}); // Colombia
        }


        return webClient.get()
                .uri("/json/" + ip)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(response -> {
                    double lat = response.get("lat").asDouble();
                    double lon = response.get("lon").asDouble();
                    log.info("IP {} resolved to lat={}, lng={}", ip, lat, lon);
                    return new double[]{lat, lon};
                })
                .onErrorResume(e -> {
                    log.error("Error resolving IP: {}", e.getMessage());
                    // coordenadas por defecto — Colombia
                    return Mono.just(new double[]{4.5709, -74.2973});
                });
    }
}
