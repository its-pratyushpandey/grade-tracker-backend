package com.codealpha.gradetracker.controller;

import com.codealpha.gradetracker.dto.AuthRequest;
import com.codealpha.gradetracker.dto.AuthResponse;
import com.codealpha.gradetracker.model.User;
import com.codealpha.gradetracker.repository.UserRepository;
import com.codealpha.gradetracker.security.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication endpoints")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001", "http://localhost:3002", "http://localhost:5173"})
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    @Operation(summary = "Login", description = "Authenticate user and return JWT token")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        User user = (User) authentication.getPrincipal();
        String token = jwtTokenProvider.generateToken(user.getUsername(), user.getRole().name());

        AuthResponse response = AuthResponse.builder()
                .token(token)
                .type("Bearer")
                .username(user.getUsername())
                .fullName(user.getFullName())
                .role(user.getRole().name())
                .expiresIn(jwtTokenProvider.getExpirationTime())
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    @Operation(summary = "Register", description = "Register a new user")
    public ResponseEntity<String> register(@Valid @RequestBody AuthRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            return ResponseEntity.badRequest().body("Username already exists");
        }

        String fullName = request.getFullName() != null && !request.getFullName().isEmpty() 
            ? request.getFullName() 
            : request.getUsername();

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(fullName)
                .role(User.Role.ROLE_USER)
                .enabled(true)
                .build();

        userRepository.save(user);
        return ResponseEntity.ok("User registered successfully");
    }
}
