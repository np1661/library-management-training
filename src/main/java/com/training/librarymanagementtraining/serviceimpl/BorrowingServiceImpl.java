 package com.training.librarymanagementtraining.serviceimpl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.stereotype.Service;

import com.training.librarymanagementtraining.dto.BorrowingRequest;
import com.training.librarymanagementtraining.dto.BorrowingResponse;
import com.training.librarymanagementtraining.entity.Borrowing;
import com.training.librarymanagementtraining.exception.BorrowingNotFoundException;
import com.training.librarymanagementtraining.repository.BorrowingRepository;
import com.training.librarymanagementtraining.service.BorrowingService;

@Service
public class BorrowingServiceImpl implements BorrowingService {

    private final BorrowingRepository borrowingRepository;

    public BorrowingServiceImpl(BorrowingRepository borrowingRepository) {
        this.borrowingRepository = borrowingRepository;
    }

    @Override
    public BorrowingResponse createBorrowing(BorrowingRequest request) {

        validateBorrowingRequest(request);

        Borrowing borrowing = new Borrowing();

        borrowing.setMemberId(request.getMemberId());
        borrowing.setBookId(request.getBookId());
        borrowing.setBookName(request.getBookName());
        borrowing.setBookPrice(request.getBookPrice());
        borrowing.setBorrowedDate(request.getBorrowedDate());
        borrowing.setDueDate(request.getDueDate());
        borrowing.setReturnedDate(request.getReturnedDate());
        borrowing.setAllowedDays(request.getAllowedDays());
        borrowing.setPenaltyPerDay(request.getPenaltyPerDay());
        borrowing.setStatus(request.getStatus());

        calculatePenalty(borrowing);

        Borrowing savedBorrowing = borrowingRepository.save(borrowing);

        return mapToResponse(savedBorrowing);
    }

    @Override
    public List<BorrowingResponse> getAllBorrowings() {

        return borrowingRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public BorrowingResponse getBorrowingById(Long id) {

        Borrowing borrowing = borrowingRepository.findById(id)
                .orElseThrow(() ->
                        new BorrowingNotFoundException(
                                "Borrowing not found with id: " + id));

        return mapToResponse(borrowing);
    }

    @Override
    public BorrowingResponse updateBorrowing(Long id, BorrowingRequest request) {

        validateBorrowingRequest(request);

        Borrowing borrowing = borrowingRepository.findById(id)
                .orElseThrow(() ->
                        new BorrowingNotFoundException(
                                "Borrowing not found with id: " + id));

        borrowing.setMemberId(request.getMemberId());
        borrowing.setBookId(request.getBookId());
        borrowing.setBookName(request.getBookName());
        borrowing.setBookPrice(request.getBookPrice());
        borrowing.setBorrowedDate(request.getBorrowedDate());
        borrowing.setDueDate(request.getDueDate());
        borrowing.setReturnedDate(request.getReturnedDate());
        borrowing.setAllowedDays(request.getAllowedDays());
        borrowing.setPenaltyPerDay(request.getPenaltyPerDay());
        borrowing.setStatus(request.getStatus());

        calculatePenalty(borrowing);

        Borrowing updatedBorrowing = borrowingRepository.save(borrowing);

        return mapToResponse(updatedBorrowing);
    }

    @Override
    public void deleteBorrowing(Long id) {

        if (!borrowingRepository.existsById(id)) {
            throw new BorrowingNotFoundException(
                    "Borrowing not found with id: " + id);
        }

        borrowingRepository.deleteById(id);
    }

    @Override
    public List<BorrowingResponse> searchByStatus(String status) {

        return borrowingRepository.findByStatusIgnoreCase(status)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private void validateBorrowingRequest(BorrowingRequest request) {

        if (request.getMemberId() == null) {
            throw new IllegalArgumentException("Member ID cannot be null");
        }

        if (request.getBookId() == null) {
            throw new IllegalArgumentException("Book ID cannot be null");
        }

        if (request.getBookName() == null || request.getBookName().isBlank()) {
            throw new IllegalArgumentException("Book name cannot be empty");
        }

        if (request.getBookPrice() == null ||
                request.getBookPrice().compareTo(BigDecimal.ZERO) < 0) {

            throw new IllegalArgumentException(
                    "Book price cannot be negative");
        }

        if (request.getBorrowedDate() == null) {
            throw new IllegalArgumentException(
                    "Borrowed date cannot be null");
        }

        if (request.getDueDate() == null) {
            throw new IllegalArgumentException(
                    "Due date cannot be null");
        }

        if (request.getDueDate().isBefore(request.getBorrowedDate())) {
            throw new IllegalArgumentException(
                    "Due date cannot be before borrowed date");
        }

        if (request.getPenaltyPerDay() == null ||
                request.getPenaltyPerDay().compareTo(BigDecimal.ZERO) < 0) {

            throw new IllegalArgumentException(
                    "Penalty per day cannot be negative");
        }
    }

    private void calculatePenalty(Borrowing borrowing) {

        LocalDate dueDate = borrowing.getDueDate();
        LocalDate returnedDate = borrowing.getReturnedDate();
        BigDecimal penaltyPerDay = borrowing.getPenaltyPerDay();

        if (dueDate == null || penaltyPerDay == null) {
            borrowing.setExtraDays(0);
            borrowing.setTotalPenalty(BigDecimal.ZERO);
            return;
        }

        LocalDate calculationDate;

        if (returnedDate != null) {
            calculationDate = returnedDate;
        } else {
            calculationDate = LocalDate.now();
        }

        long daysLate = ChronoUnit.DAYS.between(dueDate, calculationDate);

        int extraDays = (int) Math.max(daysLate, 0);

        BigDecimal totalPenalty =
                penaltyPerDay.multiply(BigDecimal.valueOf(extraDays));

        borrowing.setExtraDays(extraDays);
        borrowing.setTotalPenalty(totalPenalty);
    }

    private BorrowingResponse mapToResponse(Borrowing borrowing) {

        BorrowingResponse response = new BorrowingResponse();

        response.setId(borrowing.getId());
        response.setMemberId(borrowing.getMemberId());
        response.setBookId(borrowing.getBookId());
        response.setBookName(borrowing.getBookName());
        response.setBookPrice(borrowing.getBookPrice());
        response.setBorrowedDate(borrowing.getBorrowedDate());
        response.setDueDate(borrowing.getDueDate());
        response.setReturnedDate(borrowing.getReturnedDate());
        response.setAllowedDays(borrowing.getAllowedDays());
        response.setPenaltyPerDay(borrowing.getPenaltyPerDay());
        response.setExtraDays(borrowing.getExtraDays());
        response.setTotalPenalty(borrowing.getTotalPenalty());
        response.setStatus(borrowing.getStatus());

        return response;
    }
}

