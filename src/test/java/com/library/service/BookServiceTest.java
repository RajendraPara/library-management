package com.library.service;

import com.library.dto.BookRequest;
import com.library.dto.BookResponse;
import com.library.entity.Book;
import com.library.entity.BookIssue;
import com.library.entity.User;
import com.library.exception.ConflictException;
import com.library.exception.ResourceNotFoundException;
import com.library.repository.BookIssueRepository;
import com.library.repository.BookRepository;
import com.library.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock private BookRepository bookRepository;
    @Mock private BookIssueRepository bookIssueRepository;
    @Mock private UserRepository userRepository;
    @Mock private SecurityContext securityContext;
    @Mock private Authentication authentication;

    @InjectMocks
    private BookService bookService;

    @BeforeEach
    void setupSecurity() {
        // This allows us to mock the "logged in user"
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void addBook_Success() {
        // Arrange
        BookRequest request = new BookRequest("Clean Code", "Robert Martin", "12345", 10, "Tech");
        Book savedBook = new Book();
        savedBook.setId(1L);
        savedBook.setTitle("Clean Code");
        savedBook.setAvailableCopies(10);

        when(bookRepository.save(any(Book.class))).thenReturn(savedBook);

        // Act
        BookResponse response = bookService.addBook(request);

        // Assert
        assertNotNull(response);
        assertEquals("Clean Code", response.getTitle());
        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    void issueBook_Success() {
        // Arrange
        Long bookId = 1L;
        User user = new User();
        user.setUsername("testuser");

        Book book = new Book();
        book.setId(bookId);
        book.setAvailableCopies(5);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(bookIssueRepository.findByUserAndBookIdAndStatus(any(), any(), any())).thenReturn(Optional.empty());

        // Act
        String result = bookService.issueBook(bookId);

        // Assert
        assertEquals("Book issued successfully", result);
        assertEquals(4, book.getAvailableCopies()); // Logic check: copies should decrease
        verify(bookIssueRepository).save(any(BookIssue.class));
    }

    @Test
    void issueBook_ThrowsConflict_WhenNoCopiesLeft() {
        // Arrange
        Long bookId = 1L;
        Book book = new Book();
        book.setAvailableCopies(0);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(new User()));
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

        // Act & Assert
        assertThrows(ConflictException.class, () -> bookService.issueBook(bookId));
    }

    @Test
    void deleteBook_ThrowsConflict_WhenBookIsIssued() {
        // Arrange
        Book book = new Book();
        book.setTotalCopies(10);
        book.setAvailableCopies(9); // 1 copy is out

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        // Act & Assert
        assertThrows(ConflictException.class, () -> bookService.deleteBook(1L));
    }
}