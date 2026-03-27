package com.brayanpv.app-microservice-location-producer.controller.contracts;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

public interface IExampleController {

    Mono<ResponseEntity> getExampe();
}