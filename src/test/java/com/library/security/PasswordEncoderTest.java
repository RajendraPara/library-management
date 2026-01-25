package com.library.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

class PasswordEncoderTest {

    private final PasswordEncoder encoder = new BCryptPasswordEncoder();

    @Test
    void passwordEncoder_ShouldHashAndMatch() {
        String rawPassword = "mySecurePassword123";

        // Act
        String encoded = encoder.encode(rawPassword);

        // Assert
        assertNotNull(encoded);
        assertNotEquals(rawPassword, encoded); // Ensure it's not plain text
        assertTrue(encoder.matches(rawPassword, encoded)); // Ensure it can be verified
    }

    @Test
    void passwordEncoder_ShouldGenerateDifferentHashesForSamePassword() {
        String password = "samePassword";

        // BCrypt uses a "salt", so every hash should be unique
        String hash1 = encoder.encode(password);
        String hash2 = encoder.encode(password);

        assertNotEquals(hash1, hash2);
    }
}