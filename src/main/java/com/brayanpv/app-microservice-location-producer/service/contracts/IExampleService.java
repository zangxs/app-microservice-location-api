package com.brayanpv.app-microservice-location-producer.service.contracts;

import com.brayanpv.app-microservice-location-producer.model.response.ExampleResponse;
import reactor.core.publisher.Mono;

public interface IExampleService {

    Mono<ExampleResponse> getExample();
}