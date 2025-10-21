package com.ey.foodEase.controller;

import com.ey.foodEase.response.dto.RatingResponse;
import com.ey.foodEase.service.RatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ratings")
public class RatingController {

    @Autowired
    private RatingService ratingService;

    // ✅ Get all ratings submitted by a customer
    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/user/{username}")
    public List<RatingResponse> getRatingsByUser(@PathVariable String username) {
        return ratingService.getRatingsByUser(username);
    }
}