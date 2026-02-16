package com.example.demo.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record BookDTO(
        @NotBlank(message="Book must have a title")
        String title,

        @NotBlank(message="Book must have an author")
        String author,

        @Positive(message="Book must have total # of chapters")
        int totalChapters
) {}
