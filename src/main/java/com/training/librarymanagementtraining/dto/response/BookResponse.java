package com.training.librarymanagementtraining.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookResponse {

    private Long bookId;

    private String bookName;

    private String bookAuthor;

    private String category;

    private Boolean available;

}
