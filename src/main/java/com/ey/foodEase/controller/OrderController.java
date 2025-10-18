package com.ey.foodEase.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ey.foodEase.model.Order;
import com.ey.foodEase.repository.RestaurantRepository;
import com.ey.foodEase.request.OrderItemRequest;
import com.ey.foodEase.request.OrderRequest;
import com.ey.foodEase.response.OrderResponse;
import com.ey.foodEase.service.OrderService;
import com.ey.foodEase.util.JwtUtil;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;
    
    @Autowired
    private RestaurantRepository restaurantRepo;
    
    @Autowired
    private JwtUtil jwtUtil;

    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping("/draft")
    public ResponseEntity<OrderResponse> createDraftOrder(@RequestBody OrderRequest request,
                                                          @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        return ResponseEntity.ok(orderService.createDraftOrder(request, token));
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping("/{orderId}/items")
    public ResponseEntity<Void> addItemToDraft(@PathVariable Long orderId,
                                               @RequestBody OrderItemRequest item,
                                               @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        orderService.addItemToDraft(orderId, item, token);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @PutMapping("/{orderId}/items/{itemId}")
    public ResponseEntity<Void> updateItemQuantity(@PathVariable Long orderId,
                                                   @PathVariable Long itemId,
                                                   @RequestParam int quantity,
                                                   @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        orderService.updateItemQuantity(orderId, itemId, quantity, token);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @DeleteMapping("/{orderId}/items/{itemId}")
    public ResponseEntity<Void> removeItemFromDraft(@PathVariable Long orderId,
                                                    @PathVariable Long itemId,
                                                    @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        orderService.removeItemFromDraft(orderId, itemId, token);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping("/{orderId}/place")
    public ResponseEntity<OrderResponse> placeDraftOrder(@PathVariable Long orderId,
                                                         @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        return ResponseEntity.ok(orderService.placeDraftOrder(orderId, token));
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @PutMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelOrder(@PathVariable Long id,
                                            @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        orderService.cancelOrder(id, token);
        return ResponseEntity.ok().build();
    }
    
    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<OrderResponse>> getOrdersByCustomer(
            @PathVariable Long customerId,
            @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        return ResponseEntity.ok(orderService.getOrdersByCustomer(customerId, token));
    }
    
    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/customer/{customerId}/current")
    public ResponseEntity<OrderResponse> getCurrentOrder(
            @PathVariable Long customerId,
            @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        return ResponseEntity.ok(orderService.getCurrentOrder(customerId, token));
    }
    
    @PreAuthorize("hasRole('OWNER')")
    @PutMapping("/{orderId}/approve")
    public ResponseEntity<Void> approveOrder(
            @PathVariable Long orderId,
            @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        orderService.approveOrder(orderId, token);
        return ResponseEntity.ok().build();
    }
    
    @PreAuthorize("hasRole('OWNER')")
    @GetMapping("/owner/pending")
    public ResponseEntity<List<OrderResponse>> getPendingOrdersForOwner(
            @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        Long ownerId = jwtUtil.extractUserId(token);
        return ResponseEntity.ok(orderService.getPendingOrdersForOwner(ownerId));
    }
    
}