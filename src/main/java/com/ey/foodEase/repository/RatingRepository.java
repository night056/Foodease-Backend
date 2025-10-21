package com.ey.foodEase.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ey.foodEase.model.Rating;
import com.ey.foodEase.model.User;

public interface RatingRepository extends JpaRepository<Rating, Long> {
    List<Rating> findByRestaurantId(Long restaurantId);
    List<Rating> findByUser(User user);
}