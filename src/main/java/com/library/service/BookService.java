package com.library.service;

import com.library.dto.BookRequest;
import com.library.dto.BookResponse;
import com.library.dto.UserSummaryDTO;
import com.library.entity.Book;
import com.library.entity.BookIssue;
import com.library.entity.User;
import com.library.exception.BadRequestException;
import com.library.exception.ConflictException;
import com.library.exception.ForbiddenException;
import com.library.exception.ResourceNotFoundException;
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

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookIssueRepository bookIssueRepository;

    @Autowired
    private UserRepository userRepository;

    // ================= ADD BOOK =================
    public BookResponse addBook(BookRequest request) {

        if (request.getTitle() == null || request.getTotalCopies() <= 0) {
            throw new BadRequestException("Invalid book details");
        }

        Book book = new Book();
        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setIsbn(request.getIsbn());
        book.setTotalCopies(request.getTotalCopies());
        book.setAvailableCopies(request.getTotalCopies());
        book.setDescription(request.getDescription());

        return mapToResponse(bookRepository.save(book));
    }

    // ================= UPDATE BOOK =================
    public BookResponse updateBook(Long id, BookRequest request) {

        Book book = bookRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Book not found with id: " + id));

        int difference = request.getTotalCopies() - book.getTotalCopies();
        if (book.getAvailableCopies() + difference < 0) {
            throw new BadRequestException("Total copies cannot be less than issued copies");
        }

        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setIsbn(request.getIsbn());
        book.setTotalCopies(request.getTotalCopies());
        book.setAvailableCopies(book.getAvailableCopies() + difference);
        book.setDescription(request.getDescription());

        return mapToResponse(bookRepository.save(book));
    }

    // ================= DELETE BOOK =================
    public void deleteBook(Long id) {

        Book book = bookRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Book not found with id: " + id));

        if (book.getAvailableCopies() < book.getTotalCopies()) {
            throw new ConflictException(
                    "Cannot delete book. Some copies are currently issued");
        }

        bookRepository.delete(book);
    }

    // ================= GET BOOKS =================
    public List<BookResponse> getAllBooks() {
        return bookRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public BookResponse getBookById(Long id) {
        return bookRepository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Book not found with id: " + id));
    }

    // ================= ISSUE BOOK (CURRENT USER) =================
    @Transactional
    public String issueBook(Long bookId) {

        User user = getCurrentUser();

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Book not found"));

        if (book.getAvailableCopies() <= 0) {
            throw new ConflictException("Book is not available");
        }

        if (bookIssueRepository
                .findByUserAndBookIdAndStatus(user, bookId, BookIssue.Status.ISSUED)
                .isPresent()) {

            throw new ConflictException("You have already issued this book");
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

    // ================= USER ISSUED BOOKS =================
    public List<Long> getUserIssuedBookIds() {

        User user = getCurrentUser();

        return bookIssueRepository
                .findByUserAndStatus(user, BookIssue.Status.ISSUED)
                .stream()
                .map(issue -> issue.getBook().getId())
                .collect(Collectors.toList());
    }

    // ================= ISSUE BOOK TO USER (ADMIN) =================
    @Transactional
    public String issueBookToUser(Long bookId, Long userId) {

        User targetUser = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Book not found"));

        if (book.getAvailableCopies() <= 0) {
            throw new ConflictException("Book is not available");
        }

        if (bookIssueRepository
                .findByUserAndBookIdAndStatus(
                        targetUser, bookId, BookIssue.Status.ISSUED)
                .isPresent()) {

            throw new ConflictException("User has already issued this book");
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

    // ================= RETURN BOOK =================
    @Transactional
    public String returnBook(Long bookId) {

        User user = getCurrentUser();

        BookIssue issue = bookIssueRepository
                .findByUserAndBookIdAndStatus(user, bookId, BookIssue.Status.ISSUED)
                .orElseThrow(() ->
                        new ForbiddenException("No active issue found for this book"));

        Book book = issue.getBook();
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        bookRepository.save(book);

        issue.setReturnDate(LocalDateTime.now());
        issue.setStatus(BookIssue.Status.RETURNED);
        bookIssueRepository.save(issue);

        return "Book returned successfully";
    }

    // ================= ADMIN USERS =================
    public List<UserSummaryDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(user -> new UserSummaryDTO(
                        user.getId(),
                        user.getUsername(),
                        user.getEmail(),
                        user.getRole().name()))
                .collect(Collectors.toList());
    }

    // ================= HELPERS =================
    private User getCurrentUser() {
        String username =
                SecurityContextHolder.getContext().getAuthentication().getName();

        return userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Authenticated user not found"));
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

