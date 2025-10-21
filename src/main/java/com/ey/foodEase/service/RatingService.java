package com.ey.foodEase.service;

import com.ey.foodEase.model.Rating;
import com.ey.foodEase.model.User;
import com.ey.foodEase.repository.RatingRepository;
import com.ey.foodEase.repository.UserRepository;
import com.ey.foodEase.response.dto.RatingResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RatingService {

    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private UserRepository userRepository;

    public List<RatingResponse> getRatingsByUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Rating> ratings = ratingRepository.findByUser(user);

        return ratings.stream()
                .map(rating -> new RatingResponse(
                        rating.getId(),
                        rating.getScore(),
                        rating.getRestaurant().getName()
                ))
                .collect(Collectors.toList());
    }
}