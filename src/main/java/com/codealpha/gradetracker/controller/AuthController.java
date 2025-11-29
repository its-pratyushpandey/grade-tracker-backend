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
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@Slf4j
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
        log.info("🔐 Login attempt for user: {}", request.getUsername());
        
        try {
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

            log.info("✅ Login successful for user: {}", request.getUsername());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("❌ Login failed for user: {}", request.getUsername(), e);
            throw e;
        }
    }

    @PostMapping("/register")
    @Operation(summary = "Register", description = "Register a new user")
    public ResponseEntity<String> register(@Valid @RequestBody AuthRequest request) {
        log.info("📝 Registration attempt for user: {}", request.getUsername());
        
        try {
            // Validate username
            if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
                log.error("❌ Registration failed: username is empty");
                return ResponseEntity.badRequest().body("Username is required");
            }
            
            // Validate password
            if (request.getPassword() == null || request.getPassword().length() < 6) {
                log.error("❌ Registration failed: password too short");
                return ResponseEntity.badRequest().body("Password must be at least 6 characters");
            }
            
            // Check if user exists
            if (userRepository.existsByUsername(request.getUsername())) {
                log.error("❌ Registration failed: username already exists: {}", request.getUsername());
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
            log.info("✅ User registered successfully: {}", request.getUsername());
            return ResponseEntity.ok("User registered successfully");
        } catch (Exception e) {
            log.error("❌ Registration failed for user: {}", request.getUsername(), e);
            return ResponseEntity.badRequest().body("Registration failed: " + e.getMessage());
        }
    }
}
