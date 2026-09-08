package com.training.librarymanagementtraining.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateRequest {

    @NotBlank(message = "Title name is required")
    private String title;

    @NotBlank(message = "Author name is required")
    private String author;

    @NotBlank(message = "Category is required")
    private String category;

}
