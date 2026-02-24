package com.example.demo.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterDTO(
        @NotBlank(message = "User must have username")
        @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
        String username,

        @NotBlank(message = "User must have email")
        String email,

        @NotBlank(message = "User must have password")
        @Size(min = 3, max = 20, message = "Password must be between 3 and 20 characters")
        String password
) {}
