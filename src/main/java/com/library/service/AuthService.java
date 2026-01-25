package com.library.service;

import com.library.dto.AuthResponse;
import com.library.dto.ForgotPasswordRequest;
import com.library.dto.LoginRequest;
import com.library.dto.RegisterRequest;
import com.library.entity.User;
import com.library.exception.BadRequestException;
import com.library.exception.ConflictException;
import com.library.exception.ResourceNotFoundException;
import com.library.exception.UnauthorizedException;
import com.library.repository.UserRepository;
import com.library.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    //  REGISTER
    public AuthResponse register(RegisterRequest request) {

        if (request.getUsername() == null || request.getPassword() == null) {
            throw new BadRequestException("Username and password are required");
        }

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ConflictException("Username already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setRole(
                request.getRole() != null ? request.getRole() : User.Role.USER
        );

        userRepository.save(user);

        UserDetails userDetails =
                userDetailsService.loadUserByUsername(user.getUsername());

        String token = jwtUtil.generateToken(userDetails);

        return new AuthResponse(
                token,
                user.getUsername(),
                user.getRole().name(),
                "User registered successfully"
        );
    }

    //  LOGIN
    public AuthResponse login(LoginRequest request) {

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );
        } catch (BadCredentialsException ex) {
            throw new UnauthorizedException("Invalid username or password");
        }

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        UserDetails userDetails =
                userDetailsService.loadUserByUsername(request.getUsername());

        String token = jwtUtil.generateToken(userDetails);

        return new AuthResponse(
                token,
                user.getUsername(),
                user.getRole().name(),
                "Login successful"
        );
    }

    // FORGOT PASSWORD
    public AuthResponse resetPassword(ForgotPasswordRequest request) {

        if (request.getEmail() == null || request.getNewPassword() == null) {
            throw new BadRequestException("Email and new password are required");
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new ResourceNotFoundException("No user found with this email"));

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        return new AuthResponse(
                null,
                user.getUsername(),
                user.getRole().name(),
                "Password reset successfully. Please login with your new password."
        );
    }
}

