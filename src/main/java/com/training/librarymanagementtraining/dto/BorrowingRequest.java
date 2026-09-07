package com.training.librarymanagementtraining.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BorrowingRequest {

    private Long memberId;

    private Long bookId;

    private String bookName;

    private BigDecimal bookPrice;

    private LocalDate borrowedDate;

    private LocalDate dueDate;

    private LocalDate returnedDate;

    private Integer allowedDays;

    private BigDecimal penaltyPerDay;

    private Integer extraDays;

    private String status;
}