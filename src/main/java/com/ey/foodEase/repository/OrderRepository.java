package com.ey.foodEase.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ey.foodEase.model.Order;
import com.ey.foodEase.model.OrderStatus;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByCustomerId(Long customerId);
    List<Order> findByRestaurantId(Long restaurantId);
    List<Order> findByRestaurantIdInAndStatus(List<Long> restaurantIds, OrderStatus status);
}