package com.brayanpv.app.service.implementations;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JWTServiceTest {

    private JWTService jwtService;

    private String secret;
    private String validToken;
    private SecretKey secretKey;

    @BeforeEach
    void setUp() {
        secret = "c2VjcmV0S2V5VGhhdElzTG9uZ0Vub3VnaEZvckhTSG1hYzI1Ng==";
        secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));

        jwtService = new JWTService();
        ReflectionTestUtils.setField(jwtService, "secret", secret);
        ReflectionTestUtils.setField(jwtService, "expiration", 3600000L);

        validToken = Jwts.builder()
                .subject("user-123")
                .claim("id", "user-123")
                .claim("email", "test@example.com")
                .claim("role", "admin")
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plusSeconds(3600)))
                .signWith(secretKey)
                .compact();
    }

    @Test
    void validateTokenValid() {
        assertTrue(jwtService.validateToken(validToken));
    }

    @Test
    void validateTokenInvalid() {
        assertFalse(jwtService.validateToken("invalid.token.here"));
    }

    @Test
    void validateTokenTampered() {
        String tampered = validToken.substring(0, validToken.length() - 5) + "XXXXX";
        assertFalse(jwtService.validateToken(tampered));
    }

    @Test
    void validateTokenExpired() {
        String expiredToken = Jwts.builder()
                .subject("user-123")
                .claim("id", "user-123")
                .issuedAt(Date.from(Instant.now().minusSeconds(7200)))
                .expiration(Date.from(Instant.now().minusSeconds(3600)))
                .signWith(secretKey)
                .compact();

        assertFalse(jwtService.validateToken(expiredToken));
    }

    @Test
    void validateTokenEmpty() {
        assertThrows(IllegalArgumentException.class, () -> jwtService.validateToken(""));
    }

    @Test
    void validateTokenNull() {
        assertThrows(IllegalArgumentException.class, () -> jwtService.validateToken(null));
    }

    @Test
    void extractUserId() {
        assertEquals("user-123", jwtService.extractUserId(validToken));
    }

    @Test
    void extractEmail() {
        assertEquals("test@example.com", jwtService.extractEmail(validToken));
    }

    @Test
    void extractField() {
        assertEquals("admin", jwtService.extractField(validToken, "role"));
    }

    @Test
    void extractFieldNonExistent() {
        assertNull(jwtService.extractField(validToken, "nonexistent"));
    }
}
