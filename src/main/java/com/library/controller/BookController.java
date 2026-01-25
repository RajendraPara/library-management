package com.library.controller;

import com.library.dto.*;
import com.library.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class BookController {

    @Autowired
    private BookService bookService;

    // ================= ADMIN BOOK MANAGEMENT =================

    @PostMapping("/admin/books")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BookResponse> addBook(@RequestBody BookRequest request) {
        return ResponseEntity.ok(bookService.addBook(request));
    }

    @PutMapping("/admin/books/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BookResponse> updateBook(
            @PathVariable Long id,
            @RequestBody BookRequest request) {

        return ResponseEntity.ok(bookService.updateBook(id, request));
    }

    @DeleteMapping("/admin/books/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.ok(Map.of("message", "Book deleted successfully"));
    }

    //  PUBLIC BOOK APIs

    @GetMapping("/books")
    public ResponseEntity<List<BookResponse>> getAllBooks() {
        return ResponseEntity.ok(bookService.getAllBooks());
    }

    @GetMapping("/books/{id}")
    public ResponseEntity<BookResponse> getBookById(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.getBookById(id));
    }

    // USER BOOK ACTIONS
    @PostMapping("/books/issue")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> issueBook(
            @RequestBody IssueBookRequest request) {

        String message = bookService.issueBook(request.getBookId());
        return ResponseEntity.ok(Map.of("message", message));
    }

    @PostMapping("/books/return")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> returnBook(
            @RequestBody IssueBookRequest request) {

        String message = bookService.returnBook(request.getBookId());
        return ResponseEntity.ok(Map.of("message", message));
    }

    @GetMapping("/books/my-issued")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<Long>> getMyIssuedBooks() {
        return ResponseEntity.ok(bookService.getUserIssuedBookIds());
    }

    // ADMIN USER ACTIONS

    @GetMapping("/admin/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserSummaryDTO>> getAllUsers() {
        return ResponseEntity.ok(bookService.getAllUsers());
    }

    @PostMapping("/admin/books/issue-to-user")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> issueBookToUser(
            @RequestBody IssueBookToUserRequest request) {

        String message = bookService.issueBookToUser(
                request.getBookId(),
                request.getUserId()
        );
        return ResponseEntity.ok(Map.of("message", message));
    }
}
