

package com.library.controller;


import com.library.dto.AuthResponse;
import com.library.dto.ForgotPasswordRequest;
import com.library.dto.LoginRequest;
import com.library.dto.RegisterRequest;
import com.library.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

    @Autowired
    private AuthService authService;

    @Test
    public void register(@RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ;
    }

    @Test void forgotPassword(@RequestBody ForgotPasswordRequest request) {
        AuthResponse response = authService.resetPassword(request);
        return;
    }

    @Test
    public void login(@RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ;
    }
}






