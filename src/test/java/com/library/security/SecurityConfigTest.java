package com.library.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityConfigTest {


    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CustomUserDetailsService userDetailsService;

    @MockitoBean
    private JwtUtil jwtUtil;

    // --- TEST PUBLIC ACCESS ---

    @Test
    void publicEndpoints_ShouldBeAccessible() throws Exception {
        // Matches your permitAll() list
        mockMvc.perform(get("/api/auth/login"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/index.html"))
                .andExpect(status().isOk());
    }

    // --- TEST PROTECTED ACCESS ---

    @Test
    void adminEndpoints_ShouldDenyAnonymous() throws Exception {
        // Should fail because no user is logged in
        mockMvc.perform(post("/api/admin/books"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    void adminEndpoints_ShouldDenyRegularUser() throws Exception {
        // Should fail because USER role != ADMIN role
        mockMvc.perform(post("/api/admin/books"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminEndpoints_ShouldAllowAdmin() throws Exception {
        // Should pass the security gate (it might 404 or 400 later,
        // but it shouldn't be 403 Forbidden)
        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isOk());
    }

    @Test
    void h2Console_ShouldBeAccessible() throws Exception {
        // Verifies the frameOptions().disable() and permitAll() for H2
        mockMvc.perform(get("/h2-console"))
                .andExpect(status().isOk());
    }
}