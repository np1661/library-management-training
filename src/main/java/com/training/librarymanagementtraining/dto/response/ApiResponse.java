package com.training.librarymanagementtraining.dto.response;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApiResponse {

    private String message;
}
