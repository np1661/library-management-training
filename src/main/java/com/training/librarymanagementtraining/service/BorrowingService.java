package com.training.librarymanagementtraining.service;

import java.util.List;

import com.training.librarymanagementtraining.dto.BorrowingRequest;
import com.training.librarymanagementtraining.dto.BorrowingResponse;

public interface BorrowingService {

    BorrowingResponse createBorrowing(BorrowingRequest request);

    List<BorrowingResponse> getAllBorrowings();

    BorrowingResponse getBorrowingById(Long id);

    BorrowingResponse updateBorrowing(Long id, BorrowingRequest request);

    void deleteBorrowing(Long id);

    List<BorrowingResponse> searchByStatus(String status);

    BorrowingResponse borrowBook(Long bookId, String username);

    BorrowingResponse returnBook(Long borrowingId, String username);

    List<BorrowingResponse> getMyBorrowings(String username);
}