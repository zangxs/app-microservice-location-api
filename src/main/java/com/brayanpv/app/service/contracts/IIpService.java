package com.brayanpv.app.service.contracts;

import reactor.core.publisher.Mono;

public interface IIpService {
    Mono<double[]> getCoordinates(String ip);
}
