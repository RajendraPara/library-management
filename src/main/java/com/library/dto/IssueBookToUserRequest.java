package com.library.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IssueBookToUserRequest {
    private Long bookId;
    private Long userId;  // Optional - if null, issue to self
}