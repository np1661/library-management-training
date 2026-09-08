package com.training.librarymanagementtraining.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.training.librarymanagementtraining.dto.request.LoginRequest;
import com.training.librarymanagementtraining.dto.response.LoginResponse;
import com.training.librarymanagementtraining.service.AuthService;

import org.springframework.http.HttpStatus;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import com.training.librarymanagementtraining.dto.request.RegisterMemberRequest;


@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @RequestBody LoginRequest request) {

        return ResponseEntity.ok(
                authService.login(request));
    }

    @PostMapping("/register-member")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> registerMember(
            @Valid @RequestBody RegisterMemberRequest request) {

        authService.registerMember(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("Member registered successfully");
    }
}