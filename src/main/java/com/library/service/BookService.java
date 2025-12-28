package com.library.service;

import com.library.dto.BookRequest;
import com.library.dto.BookResponse;
import com.library.entity.Book;
import com.library.entity.BookIssue;
import com.library.entity.User;
import com.library.repository.BookIssueRepository;
import com.library.repository.BookRepository;
import com.library.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import com.library.dto.UserSummaryDTO;

@Service
public class BookService {
    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookIssueRepository bookIssueRepository;

    @Autowired
    private UserRepository userRepository;

    public BookResponse addBook(BookRequest request) {
        Book book = new Book();
        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setIsbn(request.getIsbn());
        book.setTotalCopies(request.getTotalCopies());
        book.setAvailableCopies(request.getTotalCopies());
        book.setDescription(request.getDescription());

        book = bookRepository.save(book);
        return mapToResponse(book);
    }

    public BookResponse updateBook(Long id, BookRequest request) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setIsbn(request.getIsbn());

        int difference = request.getTotalCopies() - book.getTotalCopies();
        book.setTotalCopies(request.getTotalCopies());
        book.setAvailableCopies(book.getAvailableCopies() + difference);
        book.setDescription(request.getDescription());

        book = bookRepository.save(book);
        return mapToResponse(book);
    }

    public void deleteBook(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        if (book.getAvailableCopies() < book.getTotalCopies()) {
            throw new RuntimeException("Cannot delete book. Some copies are currently issued");
        }

        bookRepository.delete(book);
    }

    public List<BookResponse> getAllBooks() {
        return bookRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public BookResponse getBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found"));
        return mapToResponse(book);
    }

    @Transactional
    public String issueBook(Long bookId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        if (book.getAvailableCopies() <= 0) {
            throw new RuntimeException("Book is not available");
        }

        if (bookIssueRepository.findByUserAndBookIdAndStatus(user, bookId, BookIssue.Status.ISSUED).isPresent()) {
            throw new RuntimeException("You have already issued this book");
        }

        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookRepository.save(book);

        BookIssue issue = new BookIssue();
        issue.setUser(user);
        issue.setBook(book);
        issue.setIssueDate(LocalDateTime.now());
        issue.setStatus(BookIssue.Status.ISSUED);
        bookIssueRepository.save(issue);

        return "Book issued successfully";
    }

    public List<Long> getUserIssuedBookIds() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<BookIssue> issuedBooks = bookIssueRepository.findByUserAndStatus(user, BookIssue.Status.ISSUED);
        return issuedBooks.stream()
                .map(issue -> issue.getBook().getId())
                .collect(Collectors.toList());
    }

    // Add this method to issue book to specific user (Admin feature)
    @Transactional
    public String issueBookToUser(Long bookId, Long userId) {
        User targetUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        if (book.getAvailableCopies() <= 0) {
            throw new RuntimeException("Book is not available");
        }

        if (bookIssueRepository.findByUserAndBookIdAndStatus(targetUser, bookId, BookIssue.Status.ISSUED).isPresent()) {
            throw new RuntimeException("This user has already issued this book");
        }

        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookRepository.save(book);

        BookIssue issue = new BookIssue();
        issue.setUser(targetUser);
        issue.setBook(book);
        issue.setIssueDate(LocalDateTime.now());
        issue.setStatus(BookIssue.Status.ISSUED);
        bookIssueRepository.save(issue);

        return "Book issued successfully to " + targetUser.getUsername();
    }

    // Get all users (for admin to select)
    public List<UserSummaryDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> new UserSummaryDTO(
                        user.getId(),
                        user.getUsername(),
                        user.getEmail(),
                        user.getRole().name()
                ))
                .collect(Collectors.toList());
    }
    @Transactional
    public String returnBook(Long bookId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        BookIssue issue = bookIssueRepository.findByUserAndBookIdAndStatus(user, bookId, BookIssue.Status.ISSUED)
                .orElseThrow(() -> new RuntimeException("No active issue found for this book"));

        Book book = issue.getBook();
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        bookRepository.save(book);

        issue.setReturnDate(LocalDateTime.now());
        issue.setStatus(BookIssue.Status.RETURNED);
        bookIssueRepository.save(issue);

        return "Book returned successfully";
    }

    private BookResponse mapToResponse(Book book) {
        BookResponse response = new BookResponse();
        response.setId(book.getId());
        response.setTitle(book.getTitle());
        response.setAuthor(book.getAuthor());
        response.setIsbn(book.getIsbn());
        response.setTotalCopies(book.getTotalCopies());
        response.setAvailableCopies(book.getAvailableCopies());
        response.setDescription(book.getDescription());
        response.setAvailable(book.getAvailableCopies() > 0);
        return response;
    }
}