 package com.training.librarymanagementtraining.serviceimpl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.stereotype.Service;

import com.training.librarymanagementtraining.dto.BorrowingRequest;
import com.training.librarymanagementtraining.dto.BorrowingResponse;
import com.training.librarymanagementtraining.entity.Book;
import com.training.librarymanagementtraining.entity.Borrowing;
import com.training.librarymanagementtraining.entity.Member;
import com.training.librarymanagementtraining.repository.BookRepository;
import com.training.librarymanagementtraining.repository.BorrowingRepository;
import com.training.librarymanagementtraining.repository.MemberRepository;
import com.training.librarymanagementtraining.service.BorrowingService;
import org.springframework.transaction.annotation.Transactional;

import com.training.librarymanagementtraining.entity.Book;
import com.training.librarymanagementtraining.entity.User;
import com.training.librarymanagementtraining.repository.BookRepository;
import com.training.librarymanagementtraining.repository.UserRepository;

@Service
public class BorrowingServiceImpl implements BorrowingService {

    private final BorrowingRepository borrowingRepository;
    private final MemberRepository memberRepository;
    private final BookRepository bookRepository;

    public BorrowingServiceImpl(
            BorrowingRepository borrowingRepository,
            MemberRepository memberRepository,
            BookRepository bookRepository) {

        this.borrowingRepository = borrowingRepository;
        this.memberRepository = memberRepository;
        this.bookRepository = bookRepository;
    }

    @Override
    public BorrowingResponse createBorrowing(BorrowingRequest request) {

        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Member not found with id: "
                                        + request.getMemberId()));

        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Book not found with id: "
                                        + request.getBookId()));

        Borrowing borrowing = new Borrowing();

        // Setting Member and Book relationship
        borrowing.setMember(member);
        borrowing.setBook(book);

        borrowing.setBookName(request.getBookName());
        borrowing.setBookPrice(request.getBookPrice());
        borrowing.setBorrowedDate(request.getBorrowedDate());
        borrowing.setDueDate(request.getDueDate());
        borrowing.setReturnedDate(request.getReturnedDate());
        borrowing.setAllowedDays(request.getAllowedDays());
        borrowing.setPenaltyPerDay(request.getPenaltyPerDay());
        borrowing.setStatus(request.getStatus());

        calculatePenalty(borrowing);

        Borrowing savedBorrowing =
                borrowingRepository.save(borrowing);

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
                        new RuntimeException(
                                "Borrowing not found with id: " + id));

        return mapToResponse(borrowing);
    }

    @Override
    public BorrowingResponse updateBorrowing(
            Long id,
            BorrowingRequest request) {

        validateBorrowingRequest(request);

        Borrowing borrowing = borrowingRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Borrowing not found with id: " + id));

        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Member not found with id: "
                                        + request.getMemberId()));

        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Book not found with id: "
                                        + request.getBookId()));

        // Updating Member and Book relationship
        borrowing.setMember(member);
        borrowing.setBook(book);

        borrowing.setBookName(request.getBookName());
        borrowing.setBookPrice(request.getBookPrice());
        borrowing.setBorrowedDate(request.getBorrowedDate());
        borrowing.setDueDate(request.getDueDate());
        borrowing.setReturnedDate(request.getReturnedDate());
        borrowing.setAllowedDays(request.getAllowedDays());
        borrowing.setPenaltyPerDay(request.getPenaltyPerDay());
        borrowing.setStatus(request.getStatus());

        calculatePenalty(borrowing);

        Borrowing updatedBorrowing =
                borrowingRepository.save(borrowing);

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

        return borrowingRepository
                .findByStatusIgnoreCase(status)
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
        BigDecimal penaltyPerDay =
                borrowing.getPenaltyPerDay();

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

        long daysLate =
                ChronoUnit.DAYS.between(
                        dueDate,
                        calculationDate);

        int extraDays =
                (int) Math.max(daysLate, 0);

        BigDecimal totalPenalty =
                penaltyPerDay.multiply(
                        BigDecimal.valueOf(extraDays));

        borrowing.setExtraDays(extraDays);
        borrowing.setTotalPenalty(totalPenalty);
    }

    private BorrowingResponse mapToResponse(
            Borrowing borrowing) {

        BorrowingResponse response =
                new BorrowingResponse();

        response.setId(borrowing.getId());

        // Getting IDs from the relationship
        response.setMemberId(
                borrowing.getMember().getId());

        response.setBookId(
                borrowing.getBook().getId());

        response.setBookName(
                borrowing.getBookName());

        response.setBookPrice(
                borrowing.getBookPrice());

        response.setBorrowedDate(
                borrowing.getBorrowedDate());

        response.setDueDate(
                borrowing.getDueDate());

        response.setReturnedDate(
                borrowing.getReturnedDate());

        response.setAllowedDays(
                borrowing.getAllowedDays());

        response.setPenaltyPerDay(
                borrowing.getPenaltyPerDay());

        response.setExtraDays(
                borrowing.getExtraDays());

        response.setTotalPenalty(
                borrowing.getTotalPenalty());

        response.setStatus(
                borrowing.getStatus());

        return response;
    }

    @Override
    @Transactional
    public BorrowingResponse borrowBook(
            Long bookId,
            String username) {

        // Find logged-in user
        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "User not found"));

        // Make sure MEMBER has a linked Member
        if (user.getMemberId() == null) {
            throw new IllegalArgumentException(
                    "User is not linked to a library member");
        }

        // Find book
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Book not found with id: " + bookId));

        // Check availability
        if (!Boolean.TRUE.equals(book.getAvailable())) {
            throw new IllegalArgumentException(
                    "Book is currently not available");
        }

        // Create borrowing
        Borrowing borrowing = new Borrowing();

        borrowing.setMemberId(user.getMemberId());
        borrowing.setBookId(book.getId());
        borrowing.setBookName(book.getTitle());

        // Book entity currently doesn't have price
        borrowing.setBookPrice(BigDecimal.ZERO);

        LocalDate borrowedDate = LocalDate.now();

        int allowedDays = 7;

        borrowing.setBorrowedDate(borrowedDate);
        borrowing.setAllowedDays(allowedDays);
        borrowing.setDueDate(
                borrowedDate.plusDays(allowedDays));

        borrowing.setReturnedDate(null);

        BigDecimal penaltyPerDay = BigDecimal.TEN;

        borrowing.setPenaltyPerDay(penaltyPerDay);
        borrowing.setExtraDays(0);
        borrowing.setTotalPenalty(BigDecimal.ZERO);

        borrowing.setStatus("BORROWED");

        // Save borrowing
        Borrowing savedBorrowing =
                borrowingRepository.save(borrowing);

        // Mark book unavailable
        book.setAvailable(false);

        bookRepository.save(book);

        return mapToResponse(savedBorrowing);
    }
    @Override
    @Transactional
    public BorrowingResponse returnBook(
            Long borrowingId,
            String username) {

        // Find logged-in user
        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "User not found"));

        if (user.getMemberId() == null) {
            throw new IllegalArgumentException(
                    "User is not linked to a library member");
        }

        // Find borrowing belonging to this member
        Borrowing borrowing =
                borrowingRepository
                        .findByIdAndMemberId(
                                borrowingId,
                                user.getMemberId())
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Borrowing not found for this member"));

        if ("RETURNED".equalsIgnoreCase(
                borrowing.getStatus())) {

            throw new IllegalArgumentException(
                    "Book has already been returned");
        }

        // Find book
        Book book = bookRepository.findById(
                        borrowing.getBookId())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Book not found with id: "
                                        + borrowing.getBookId()));

        // Set return information
        borrowing.setReturnedDate(LocalDate.now());
        borrowing.setStatus("RETURNED");

        // Calculate late penalty
        calculatePenalty(borrowing);

        Borrowing updatedBorrowing =
                borrowingRepository.save(borrowing);

        // Make book available again
        book.setAvailable(true);

        bookRepository.save(book);

        return mapToResponse(updatedBorrowing);
    }

    @Override
    public List<BorrowingResponse> getMyBorrowings(
            String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "User not found"));

        if (user.getMemberId() == null) {
            throw new IllegalArgumentException(
                    "User is not linked to a library member");
        }

        return borrowingRepository
                .findByMemberIdOrderByBorrowedDateDesc(
                        user.getMemberId())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }
}

