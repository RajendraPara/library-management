package com.library.service;

import com.library.dto.*;
import com.library.entity.User;
import com.library.exception.*;
import com.library.repository.UserRepository;
import com.library.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserDetailsService userDetailsService;

    @InjectMocks
    private AuthService authService;

    // ================= REGISTER TESTS =================

    @Test
    void register_Success_ShouldReturnAuthResponse() {
        // Arrange
        RegisterRequest request = new RegisterRequest("john", "pass123", "john@mail.com", User.Role.USER);
        UserDetails mockDetails = mock(UserDetails.class);

        when(userRepository.existsByUsername("john")).thenReturn(false);
        when(passwordEncoder.encode("pass123")).thenReturn("encodedPass");
        when(userDetailsService.loadUserByUsername("john")).thenReturn(mockDetails);
        when(jwtUtil.generateToken(mockDetails)).thenReturn("mock-jwt-token");

        // Act
        AuthResponse result = authService.register(request);

        // Assert
        assertNotNull(result);
        assertEquals("mock-jwt-token", result.getToken());
        assertEquals("User registered successfully", result.getMessage());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_UserAlreadyExists_ShouldThrowConflictException() {
        // Arrange
        RegisterRequest request = new RegisterRequest("john", "pass123", "john@mail.com", User.Role.USER);
        when(userRepository.existsByUsername("john")).thenReturn(true);

        // Act & Assert
        assertThrows(ConflictException.class, () -> authService.register(request));
    }

    // ================= LOGIN TESTS =================

    @Test
    void login_Success_ShouldReturnToken() {
        // Arrange
        LoginRequest request = new LoginRequest("john", "pass123");
        User user = new User();
        user.setUsername("john");
        user.setRole(User.Role.USER);
        UserDetails mockDetails = mock(UserDetails.class);

        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));
        when(userDetailsService.loadUserByUsername("john")).thenReturn(mockDetails);
        when(jwtUtil.generateToken(mockDetails)).thenReturn("new-token");

        // Act
        AuthResponse response = authService.login(request);

        // Assert
        assertEquals("new-token", response.getToken());
        assertEquals("Login successful", response.getMessage());
    }

    @Test
    void login_BadCredentials_ShouldThrowUnauthorized() {
        // Arrange
        LoginRequest request = new LoginRequest("john", "wrong_pass");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        // Act & Assert
        assertThrows(UnauthorizedException.class, () -> authService.login(request));
    }

    // ================= RESET PASSWORD TESTS =================

    @Test
    void resetPassword_Success() {
        // Arrange
        ForgotPasswordRequest request = new ForgotPasswordRequest("john@mail.com", "new_pass");
        User user = new User();
        user.setEmail("john@mail.com");
        user.setRole(User.Role.USER);

        when(userRepository.findByEmail("john@mail.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("new_pass")).thenReturn("encoded_new_pass");

        // Act
        AuthResponse response = authService.resetPassword(request);

        // Assert
        assertEquals("Password reset successfully. Please login with your new password.", response.getMessage());
        verify(userRepository).save(user);
        assertEquals("encoded_new_pass", user.getPassword());
    }
}