package com.ey.foodEase.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ey.foodEase.model.Order;
import com.ey.foodEase.model.OrderStatus;
import com.ey.foodEase.model.PaymentMode;
import com.ey.foodEase.repository.OrderRepository;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @Autowired
    private OrderRepository orderRepo;

    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping("/pay/{orderId}")
    public ResponseEntity<String> Payment(@PathVariable Long orderId,
                                              @RequestParam PaymentMode mode) {
        Order order = orderRepo.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getStatus() != OrderStatus.DRAFT) {
            return ResponseEntity.badRequest().body("Order must be confirmed before payment.");
        }

        order.setPaymentMode(mode);
        order.setStatus(OrderStatus.PAID);
        orderRepo.save(order);

        return ResponseEntity.ok("Payment successful via " + mode + " for Order ID: " + orderId);
    }
}