package com.example.activities.controller;

import com.example.activities.model.User;
import com.example.activities.repository.UserRepository;
import com.example.activities.security.AuthService;
import com.example.activities.security.AuthUser;
import com.example.activities.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.io.StringWriter;
import java.io.PrintWriter;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final AuthService authService;

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllUsers(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        AuthUser authUser = authService.resolveUser(authHeader);
        if (!authService.hasAnyRole(authUser, "admin", "coordinator")) {
            return ResponseEntity.status(403).body(List.of());
        }

        List<Map<String, Object>> users = userRepository.findAll().stream()
            .map(this::toSafeUser)
            .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    @GetMapping("/students")
    public ResponseEntity<List<Map<String, Object>>> getRegisteredStudents(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        AuthUser authUser = authService.resolveUser(authHeader);
        if (!authService.hasAnyRole(authUser, "admin", "coordinator")) {
            return ResponseEntity.status(403).body(List.of());
        }

        // Admin/coordinator can view all registered student accounts
        return ResponseEntity.ok(
                userRepository.findByRoleIgnoreCase("student").stream()
                        .map(this::toSafeUser)
                        .collect(Collectors.toList())
        );
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        user.setEmail(user.getEmail().toLowerCase().trim());
        if (userRepository.findByEmailIgnoreCase(user.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email already in use"));
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("student");
        }
        user.setIsVerified(true);
        try {
            User savedUser = userRepository.save(user);
            String token = jwtUtils.generateToken(savedUser.getEmail());
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("user", savedUser);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage(), "trace", sw.toString()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email").toLowerCase().trim();
        String password = credentials.get("password");

        Optional<User> userOpt = userRepository.findByEmailIgnoreCase(email);
        if (userOpt.isPresent() && passwordEncoder.matches(password, userOpt.get().getPassword())) {
            User user = userOpt.get();
            String token = jwtUtils.generateToken(user.getEmail());
            
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("user", user);
            
            return ResponseEntity.ok(response);
        }

        return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
    }

    @GetMapping("/me")
    public ResponseEntity<?> getMe(@RequestHeader(value="Authorization", required=false) String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body(Map.of("error", "Missing token"));
        }
        String token = authHeader.substring(7);
        try {
            String email = jwtUtils.getUsernameFromToken(token);
            User user = userRepository.findByEmailIgnoreCase(email).orElse(null);
            if (user != null) {
                return ResponseEntity.ok(user);
            }
        } catch(Exception e) {}
        return ResponseEntity.status(401).body(Map.of("error", "Invalid token"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable String id) {

        AuthUser authUser = authService.resolveUser(authHeader);
        if (!authService.hasAnyRole(authUser, "admin")) {
            return ResponseEntity.status(403).body(Map.of("error", "Only admin can delete students"));
        }

        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return ResponseEntity.status(404).body(Map.of("error", "User not found"));
        }

        // Prevent accidental deletion of non-student roles
        if (!"student".equalsIgnoreCase(user.getRole())) {
            return ResponseEntity.status(403).body(Map.of("error", "You can only delete student accounts"));
        }

        userRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("status", "deleted", "id", id));
    }

    private Map<String, Object> toSafeUser(User user) {
        Map<String, Object> safeUser = new HashMap<>();
        safeUser.put("id", user.getId());
        safeUser.put("name", user.getName());
        safeUser.put("email", user.getEmail());
        safeUser.put("role", user.getRole());
        safeUser.put("isActive", user.getIsActive());
        safeUser.put("isVerified", user.getIsVerified());
        safeUser.put("createdAt", user.getCreatedAt());
        safeUser.put("updatedAt", user.getUpdatedAt());
        return safeUser;
    }
}
