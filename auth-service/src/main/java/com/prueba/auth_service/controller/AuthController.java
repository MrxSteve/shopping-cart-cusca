package com.prueba.auth_service.controller;

import com.prueba.auth_service.dto.AuthResponse;
import com.prueba.auth_service.dto.UserLoginRequest;
import com.prueba.auth_service.dto.UserRegisterRequest;
import com.prueba.auth_service.dto.UserResponse;
import com.prueba.auth_service.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody UserRegisterRequest request) {
        log.info("Register request for user: {}", request.getUsername());
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody UserLoginRequest request) {
        log.info("Login request for user: {}", request.getUsername());
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(Authentication authentication) {
        log.info("Get current user: {}", authentication.getName());
        UserResponse user = authService.getUserByUsername(authentication.getName());
        return ResponseEntity.ok(user);
    }

    @GetMapping("/{username}")
    public ResponseEntity<UserResponse> getUserByUsername(@PathVariable String username) {
        log.info("Get user by username: {}", username);
        UserResponse user = authService.getUserByUsername(username);
        return ResponseEntity.ok(user);
    }
}
