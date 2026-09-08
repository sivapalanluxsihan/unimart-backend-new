package lk.ac.kln.unimart.auth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lk.ac.kln.unimart.auth.dto.AuthResponse;
import lk.ac.kln.unimart.auth.dto.LoginRequest;
import lk.ac.kln.unimart.auth.dto.RegisterRequest;
import lk.ac.kln.unimart.auth.service.AuthService;

/**
 * Public authentication endpoints. See Guide 07 Part B (API contract) and
 * Part F (both are permitAll for POST).
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }
}
