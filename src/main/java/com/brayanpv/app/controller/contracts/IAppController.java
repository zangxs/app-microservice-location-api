package com.brayanpv.app.controller.contracts;

import com.brayanpv.app.model.request.LocationRequest;
import com.brayanpv.app.model.response.generic.ApiResponse;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

public interface IAppController {

    Mono<ResponseEntity<ApiResponse>> uploadLocation(LocationRequest locationRequest);
}
