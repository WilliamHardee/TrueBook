package com.example.demo.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginDTO(@NotBlank(message = "User must have Email")
                       @Size(min = 3, max = 20, message = "Email must be between 3 and 20 characters")
                       String email,

                       @NotBlank(message = "User must have password")
                       @Size(min = 3, max = 20, message = "Password must be between 3 and 20 characters")
                       String password) {}
