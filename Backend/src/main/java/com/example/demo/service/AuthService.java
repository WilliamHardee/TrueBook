package com.example.demo.service;

import com.example.demo.DTO.LoginDTO;
import com.example.demo.DTO.RegisterDTO;
import com.example.demo.Exceptions.UserAlreadyExistsException;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class AuthService {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private AuthenticationManager authenticationManager;
    private JwtService jwtService;


    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtService jwtService) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public void registerUser(RegisterDTO registerDTO) {
        if(userRepository.findByEmail(registerDTO.email()).isPresent()
                || userRepository.findByUsername(registerDTO.username()).isPresent()) {
            throw new UserAlreadyExistsException("User already exists");
        }

        User user = User.builder()
                .username(registerDTO.username())
                .email(registerDTO.email())
                .password(passwordEncoder.encode(registerDTO.password()))
                .source("LOCAL")
                .build();

        userRepository.save(user);
    }

    public ResponseEntity<Map<String,String>> login(LoginDTO loginDTO) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(loginDTO.email(), loginDTO.password());

        try {
            Authentication auth = authenticationManager.authenticate(token);
        }
        catch (AuthenticationException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Username or password is incorrect"));
        }

        Optional<User> user = userRepository.findByEmail(loginDTO.email());
        return user.map(value -> ResponseEntity.status(HttpStatus.OK).body(Map.of("token", jwtService.generateToken(value)))).orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Username or password is incorrect")));
    }
}
