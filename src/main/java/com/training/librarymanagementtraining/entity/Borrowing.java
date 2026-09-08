package com.training.librarymanagementtraining.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "borrowings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Borrowing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;

    private String bookName;

    private BigDecimal bookPrice;

    private LocalDate borrowedDate;

    private LocalDate dueDate;

    private LocalDate returnedDate;

    private Integer allowedDays;

    private BigDecimal penaltyPerDay;

    private Integer extraDays;

    private BigDecimal totalPenalty;

    private String status;
}