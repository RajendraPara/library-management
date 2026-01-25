package com.library.controller;

import com.library.dto.*;
import com.library.security.CustomUserDetailsService;
import com.library.security.JwtUtil;
import com.library.security.SecurityConfig;
import com.library.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
///import org.springframework.boot.test.mock.bean.MockBean;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookController.class)

@Import(SecurityConfig.class)
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookService bookService;
    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private CustomUserDetailsService userDetailsService;
    @Autowired
    private ObjectMapper objectMapper;



    @Test
    @WithMockUser(username = "user")
    void getAllBooks_ShouldReturnList() throws Exception {
        List<BookResponse> books = List.of(new BookResponse(1L, "Clean Code", "Robert Martin"));
        when(bookService.getAllBooks()).thenReturn(books);

        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].title").value("Clean Code"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void addBook_AsAdmin_ShouldReturnCreatedBook() throws Exception {
        // Arrange
        BookRequest request = new BookRequest("Refactoring", "Martin Fowler");
        BookResponse response = new BookResponse(2L, "Refactoring", "Martin Fowler");

        when(bookService.addBook(any())).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/api/admin/books")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk()) // Or isCreated() if your controller returns 201
                .andExpect(jsonPath("$.title").value("Refactoring"))
                .andExpect(jsonPath("$.author").value("Martin Fowler"));
    }

//    @Test
//    @WithMockUser(roles = "ADMIN") // Simulates an authenticated Admin
//    void addBook_AsAdmin_ShouldReturnCreatedBook() throws Exception {
//        BookRequest request = new BookRequest("Refactoring", "Martin Fowler");
//        BookResponse response = new BookResponse(2L, "Refactoring", "Martin Fowler");
//
//        when(bookService.addBook(any(BookRequest.class))).thenReturn(response);
//
//        mockMvc.perform(post("/api/admin/books")
//                        .with(csrf()) // POST requests require CSRF if enabled
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.title").value("Refactoring"));
//    }

    @Test
    @WithMockUser(roles = "USER") // Simulates a regular user trying to access admin route
    void addBook_AsUser_ShouldReturnForbidden() throws Exception {
        BookRequest request = new BookRequest("Refactoring", "Martin Fowler");

        mockMvc.perform(post("/api/admin/books")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden()); // Verifies @PreAuthorize("hasRole('ADMIN')")
    }



    @Test
    @WithMockUser(roles = "USER")
    void issueBook_AsUser_ShouldReturnSuccessMessage() throws Exception {
        IssueBookRequest request = new IssueBookRequest(1L);
        when(bookService.issueBook(1L)).thenReturn("Book issued successfully");

        mockMvc.perform(post("/api/books/issue")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Book issued successfully"));
    }
}