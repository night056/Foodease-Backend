package com.ey.foodEase.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ey.foodEase.model.Order;
import com.ey.foodEase.model.Restaurant;
import com.ey.foodEase.model.Role;
import com.ey.foodEase.model.User;
import com.ey.foodEase.repository.OrderRepository;
import com.ey.foodEase.repository.RestaurantRepository;
import com.ey.foodEase.repository.UserRepository;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private UserRepository userRepo;
    
    
    @Autowired
    private RestaurantRepository restaurantRepo;
    
    @Autowired
    private OrderRepository orderRepo;

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userRepo.findAll());
    }

    @PutMapping("/users/{id}/role")
    public ResponseEntity<String> updateUserRole(@PathVariable Long id, @RequestParam Role role) {
        User user = userRepo.findById(id).orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getRoles().contains(role)) {
            return ResponseEntity.badRequest().body("User already has role: " + role);
        }

        user.getRoles().add(role);
        userRepo.save(user);

        return ResponseEntity.ok("Role " + role + " added to user ID: " + id);
    }
    
    @DeleteMapping("/users/{id}/role")
    public ResponseEntity<String> removeUserRole(@PathVariable Long id, @RequestParam Role role) {
        User user = userRepo.findById(id).orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getRoles().contains(role)) {
            return ResponseEntity.badRequest().body("User does not have role: " + role);
        }

        user.getRoles().remove(role);
        userRepo.save(user);

        return ResponseEntity.ok("Role " + role + " removed from user ID: " + id);
    }

    @GetMapping("/restaurants")
    public ResponseEntity<List<Restaurant>> getAllRestaurants() {
        return ResponseEntity.ok(restaurantRepo.findAll());
    }
    

    @GetMapping("/orders")
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(orderRepo.findAll());
    }
}