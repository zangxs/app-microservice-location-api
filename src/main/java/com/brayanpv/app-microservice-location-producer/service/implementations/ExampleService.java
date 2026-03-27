package com.brayanpv.app-microservice-location-producer.service.implementations;
import com.brayanpv.app-microservice-location-producer.service.contracts.IExampleService;
import com.brayanpv.app-microservice-location-producer.model.response.ExampleResponse;
import reactor.core.publisher.Mono;

public class ExampleService implements IExampleService {
    @Override
    public Mono<ExampleResponse> getExample() {
        return null;
    }
}
