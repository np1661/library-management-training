package com.training.librarymanagementtraining.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.training.librarymanagementtraining.dto.BorrowingRequest;
import com.training.librarymanagementtraining.dto.BorrowingResponse;
import com.training.librarymanagementtraining.service.BorrowingService;

@RestController
@RequestMapping("/api/borrowings")
public class BorrowingController {

    private final BorrowingService borrowingService;

    public BorrowingController(BorrowingService borrowingService) {
        this.borrowingService = borrowingService;
    }

    @PostMapping("/createBorrowing")
    public ResponseEntity<BorrowingResponse> createBorrowing(
            @RequestBody BorrowingRequest request) {

        BorrowingResponse response =
                borrowingService.createBorrowing(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/getAllBorrowings")
    public ResponseEntity<List<BorrowingResponse>> getAllBorrowings() {

        return ResponseEntity.ok(
                borrowingService.getAllBorrowings());
    }

    @GetMapping("getBorrowingById/{id}")
    public ResponseEntity<BorrowingResponse> getBorrowingById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                borrowingService.getBorrowingById(id));
    }

    @PutMapping("updateBorrowingById/{id}")
    public ResponseEntity<BorrowingResponse> updateBorrowing(
            @PathVariable Long id,
            @RequestBody BorrowingRequest request) {

        return ResponseEntity.ok(
                borrowingService.updateBorrowing(id, request));
    }

    @DeleteMapping("deleteBorrowing/{id}")
    public ResponseEntity<Void> deleteBorrowing(
            @PathVariable Long id) {

        borrowingService.deleteBorrowing(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("searchByStatus/search")
    public ResponseEntity<List<BorrowingResponse>> searchByStatus(
            @RequestParam String status) {

        return ResponseEntity.ok(
                borrowingService.searchByStatus(status));
    }
}