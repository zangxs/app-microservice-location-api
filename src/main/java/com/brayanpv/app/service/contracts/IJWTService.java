package com.brayanpv.app.service.contracts;

public interface IJWTService {

    boolean validateToken(String token);
    String extractUserId(String token);
    String extractEmail(String token);
}
