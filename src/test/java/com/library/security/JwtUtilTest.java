package com.library.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    // Use a secret that is at least 32 characters long for HS256
    private final String testSecret = "myUltraSecretKeyForTestingPurposes1234567890";
    private final Long testExpiration = 3600000L; // 1 hour

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();

        // Manually injecting the @Value fields using Reflection
        ReflectionTestUtils.setField(jwtUtil, "secret", testSecret);
        ReflectionTestUtils.setField(jwtUtil, "expiration", testExpiration);
    }

    @Test
    void generateToken_ShouldCreateValidToken() {
        UserDetails userDetails = new User("libraryUser", "password", Collections.emptyList());

        String token = jwtUtil.generateToken(userDetails);

        assertNotNull(token);
        assertEquals("libraryUser", jwtUtil.extractUsername(token));
    }

    @Test
    void validateToken_ShouldReturnTrue_ForCorrectUserAndValidToken() {
        UserDetails userDetails = new User("libraryUser", "password", Collections.emptyList());
        String token = jwtUtil.generateToken(userDetails);

        Boolean isValid = jwtUtil.validateToken(token, userDetails);

        assertTrue(isValid);
    }

    @Test
    void validateToken_ShouldReturnFalse_ForDifferentUser() {
        UserDetails userDetails = new User("libraryUser", "password", Collections.emptyList());
        UserDetails wrongUser = new User("intruder", "password", Collections.emptyList());

        String token = jwtUtil.generateToken(userDetails);
        Boolean isValid = jwtUtil.validateToken(token, wrongUser);

        assertFalse(isValid);
    }

    @Test
    void extractExpiration_ShouldReturnFutureDate() {
        UserDetails userDetails = new User("test", "test", Collections.emptyList());
        String token = jwtUtil.generateToken(userDetails);

        Date expiration = jwtUtil.extractExpiration(token);

        assertTrue(expiration.after(new Date()));
    }
}