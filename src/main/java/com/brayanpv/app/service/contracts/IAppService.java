package com.brayanpv.app.service.contracts;

import com.brayanpv.app.model.request.LocationRequest;
import com.brayanpv.app.model.response.LocationResponse;
import reactor.core.publisher.Mono;

public interface IAppService {

    Mono<LocationResponse> uploadLocation(LocationRequest locationRequest);
}
