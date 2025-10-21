package com.ey.foodEase.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ey.foodEase.model.Delivery;
import com.ey.foodEase.model.DeliveryStatus;
import com.ey.foodEase.model.User;
import com.ey.foodEase.repository.DeliveryRepository;
import com.ey.foodEase.repository.UserRepository;
import com.ey.foodEase.response.dto.DeliveryResponse;
import com.ey.foodEase.service.DeliveryService;
import com.ey.foodEase.util.JwtUtil;

@RestController
@RequestMapping("/api/delivery")
public class DeliveryController {

    @Autowired
    private DeliveryService deliveryService;
    
    @Autowired
    private DeliveryRepository deliveryRepo;
    
    @Autowired
    private UserRepository userRepo;

    @Autowired
    private JwtUtil jwtUtil;

    @PreAuthorize("hasRole('DELIVERY')")
    @GetMapping("/assigned")
    public ResponseEntity<List<DeliveryResponse>> getAssignedDeliveries(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        Long userId = jwtUtil.extractUserId(token);
        return ResponseEntity.ok(deliveryService.getAssignedDeliveries(userId));
    }

    @PreAuthorize("hasRole('DELIVERY')")
    @PutMapping("/{deliveryId}/status")
    public ResponseEntity<Void> updateDeliveryStatus(
            @PathVariable Long deliveryId,
            @RequestParam DeliveryStatus status,
            @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        Long userId = jwtUtil.extractUserId(token);
        deliveryService.updateDeliveryStatus(deliveryId, status, userId);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('DELIVERY')")
    @GetMapping("/history")
    public ResponseEntity<List<DeliveryResponse>> getDeliveryHistory(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        Long userId = jwtUtil.extractUserId(token);
        return ResponseEntity.ok(deliveryService.getDeliveryHistory(userId));
    }
    
    @GetMapping("/deliveries/available")
    public List<Delivery> getAvailableDeliveries() {
        return deliveryRepo.findByStatus(DeliveryStatus.AVAILABLE);
    }
    
    @PostMapping("/deliveries/{deliveryId}/accept")
    public ResponseEntity<?> acceptDelivery(@PathVariable Long deliveryId, Principal principal) {
        User deliveryPartner = userRepo.findByUsername(principal.getName())
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        boolean success = deliveryService.acceptDelivery(deliveryId, deliveryPartner);
        if (success) {
            return ResponseEntity.ok("Delivery accepted successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body("Delivery already accepted by another partner.");
        }
    }
    
    @GetMapping("/partner/{userId}")
    @PreAuthorize("hasRole('DELIVERY')")
    public ResponseEntity<List<Delivery>> getDeliveriesByPartner(@PathVariable Long userId) {
        User deliveryPartner = userRepo.findById(userId)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        
        List<Delivery> deliveries = deliveryRepo.findByDeliveryPartner(deliveryPartner);
        return ResponseEntity.ok(deliveries);
    }
    
    @GetMapping("/track/{orderId}")
    public ResponseEntity<DeliveryResponse> trackDelivery(@PathVariable Long orderId) {
        DeliveryResponse response = deliveryService.trackDeliveryByOrderId(orderId);
        return ResponseEntity.ok(response);
    }
}
