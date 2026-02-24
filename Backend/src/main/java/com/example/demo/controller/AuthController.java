package com.example.demo.controller;


import com.example.demo.DTO.RegisterDTO;
import com.example.demo.model.User;
import com.example.demo.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class AuthController {

    AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/auth/register")
    public ResponseEntity<Void> registerUser(@Valid @RequestBody RegisterDTO registerDTO) {
        authService.registerUser(registerDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
