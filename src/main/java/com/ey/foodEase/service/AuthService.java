package com.ey.foodEase.service;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.ey.foodEase.model.Role;
import com.ey.foodEase.model.User;
import com.ey.foodEase.repository.UserRepository;
import com.ey.foodEase.request.LoginRequest;
import com.ey.foodEase.request.RegisterRequest;
import com.ey.foodEase.util.JwtUtil;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public String register(RegisterRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPhone(request.getPhone());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        Set<Role> roleSet = request.getRoles().stream()
            .map(roleStr -> {
                try {
                    return Role.valueOf(roleStr.toUpperCase());
                } catch (IllegalArgumentException e) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid role: " + roleStr);
                }
            })
            .collect(Collectors.toSet());

        user.setRoles(roleSet);
        userRepo.save(user);


        Role defaultRole = roleSet.iterator().next();
        return jwtUtil.generateToken(user.getUsername(), user.getId(), defaultRole, roleSet);
    }

    public String login(LoginRequest request) {
        User user = userRepo.findByUsername(request.getUsername())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        Role selectedRole;
        try {
            selectedRole = Role.valueOf(request.getRole().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid role selected");
        }

        if (!user.getRoles().contains(selectedRole)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User does not have the selected role");
        }

        return jwtUtil.generateToken(user.getUsername(), user.getId(), selectedRole, user.getRoles());
    }
}