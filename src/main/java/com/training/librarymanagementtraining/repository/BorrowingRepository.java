package com.training.librarymanagementtraining.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.training.librarymanagementtraining.entity.Borrowing;

public interface BorrowingRepository extends JpaRepository<Borrowing, Long> {
    List<Borrowing> findByStatusIgnoreCase(String status);

    List<Borrowing> findByMemberIdOrderByBorrowedDateDesc(Long memberId);

    Optional<Borrowing> findByIdAndMemberId(Long id, Long memberId);
}