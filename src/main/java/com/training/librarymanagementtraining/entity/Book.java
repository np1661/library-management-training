package com.training.librarymanagementtraining.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "books")
@Builder
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title",unique = true)
    private String title;

    @Column(name = "author")
    private String author;

    @Column(name = "category")
    private String category;

    @Column(name = "available")
    private Boolean available;

}
