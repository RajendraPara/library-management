package com.library.repository;

import com.library.entity.BookIssue;
import com.library.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookIssueRepository extends JpaRepository<BookIssue, Long> {
    List<BookIssue> findByUser(User user);
    List<BookIssue> findByStatus(BookIssue.Status status);
    Optional<BookIssue> findByUserAndBookIdAndStatus(User user, Long bookId, BookIssue.Status status);

    List<BookIssue> findByUserAndStatus(User user, BookIssue.Status status);
}