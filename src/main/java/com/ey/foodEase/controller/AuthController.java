package com.ey.foodEase.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ey.foodEase.model.User;
import com.ey.foodEase.repository.UserRepository;
import com.ey.foodEase.request.dto.LoginRequest;
import com.ey.foodEase.request.dto.RegisterRequest;
import com.ey.foodEase.service.AuthService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired private AuthService authService;
    @Autowired private UserRepository userRepo;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        String token = authService.register(request);
        return ResponseEntity.ok(token);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        String token = authService.login(request);
        return ResponseEntity.ok(token);
    }

    @GetMapping("/user/{username}/roles")
    public ResponseEntity<List<String>> getUserRoles(@PathVariable String username) {
        User user = userRepo.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));

        List<String> roles = user.getRoles().stream()
            .map(Enum::name)
            .collect(Collectors.toList());

        return ResponseEntity.ok(roles);
    }
}