package com.vaishnavi.giftportal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.vaishnavi.giftportal.entity.RevokedToken;
import com.vaishnavi.giftportal.entity.Role;
import com.vaishnavi.giftportal.entity.User;
import com.vaishnavi.giftportal.repository.RevokedTokenRepository;
import com.vaishnavi.giftportal.repository.UserRepository;
import com.vaishnavi.giftportal.security.JwtUtil;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RevokedTokenRepository revokedTokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email already exists!");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.getRole() == null) {
            user.setRole(Role.USER);
        }

        userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully!");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        String password = payload.get("password");

        if (email == null || password == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email and password are required.");
        }

        Optional<User> existingUser = userRepository.findByEmail(email);
        if (existingUser.isPresent() && passwordEncoder.matches(password, existingUser.get().getPassword())) {
            User u = existingUser.get();
            return ResponseEntity.ok(Map.of(
                    "accessToken", jwtUtil.generateToken(u.getEmail(), u.getRole().name()),
                    "refreshToken", jwtUtil.generateRefreshToken(u.getEmail())
            ));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader(value = "Authorization", required = false) String authHeader,
                                    @RequestBody(required = false) Map<String, String> request) {
        // Revoke access token if present
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String accessToken = authHeader.substring(7);
            revokedTokenRepository.save(new RevokedToken(accessToken));
        }

        // Revoke refresh token if provided
        if (request != null) {
            String refreshToken = request.get("refreshToken");
            if (refreshToken != null && !refreshToken.isBlank()) {
                revokedTokenRepository.save(new RevokedToken(refreshToken));
            }
        }

        return ResponseEntity.ok("Logged out successfully.");
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        if (refreshToken == null || refreshToken.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Refresh token is required.");
        }

        // Check if the refresh token has been revoked
        if (revokedTokenRepository.findByToken(refreshToken).isPresent()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh token has been revoked.");
        }

        try {
            // Will throw if invalid/expired
            if (jwtUtil.isTokenExpired(refreshToken)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Expired refresh token.");
            }

            String email = jwtUtil.getUsernameFromToken(refreshToken);
            Optional<User> userOpt = userRepository.findByEmail(email);
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User no longer exists.");
            }

            User u = userOpt.get();
            String newAccess = jwtUtil.generateToken(u.getEmail(), u.getRole().name());
            return ResponseEntity.ok(Map.of("accessToken", newAccess));

        } catch (ExpiredJwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Expired refresh token.");
        } catch (JwtException | IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token.");
        }
    }
}
