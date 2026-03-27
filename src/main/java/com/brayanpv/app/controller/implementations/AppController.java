package com.brayanpv.app.controller.implementations;

import com.brayanpv.app.controller.contracts.IAppController;
import com.brayanpv.app.model.request.LocationRequest;
import com.brayanpv.app.model.response.generic.ApiResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@Log4j2
public class AppController implements IAppController {
    @Override
    public Mono<ResponseEntity<ApiResponse>> uploadLocation(LocationRequest locationRequest) {
        return null;
    }
}
