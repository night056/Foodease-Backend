package com.ey.foodEase.service;

import com.ey.foodEase.model.Restaurant;
import com.ey.foodEase.model.User;
import com.ey.foodEase.repository.RestaurantRepository;
import com.ey.foodEase.repository.UserRepository;
import com.ey.foodEase.request.dto.RestaurantRequest;
import com.ey.foodEase.request.dto.RestaurantResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RestaurantService {

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private UserRepository userRepository;

    public RestaurantResponse addRestaurant(RestaurantRequest request) {
        Optional<User> optionalOwner = userRepository.findById(Long.valueOf(request.getOwnerId()));
        User owner = optionalOwner.orElseThrow(() ->
                new RuntimeException("Owner not found with ID: " + request.getOwnerId()));

        Restaurant restaurant = new Restaurant();
        restaurant.setName(request.getName());
        restaurant.setAddress(request.getAddress());
        restaurant.setOwner(owner);
        restaurant.setRating(BigDecimal.ZERO); // default rating

        Restaurant saved = restaurantRepository.save(restaurant);
        return convertToResponse(saved);
    }

    public List<RestaurantResponse> getAllRestaurants() {
        return restaurantRepository.findAll()
                                   .stream()
                                   .map(this::convertToResponse)
                                   .collect(Collectors.toList());
    }

    public List<RestaurantResponse> getRestaurantsByOwnerId(Long ownerId) {
        return restaurantRepository.findByOwnerId(ownerId)
                                   .stream()
                                   .map(this::convertToResponse)
                                   .collect(Collectors.toList());
    }

    public RestaurantResponse updateRestaurant(long id, RestaurantRequest request) {
        Restaurant existing = restaurantRepository.findById(id)
                                                  .orElseThrow(() ->
                                                          new RuntimeException("Restaurant not found with ID: " + id));

        existing.setName(request.getName());
        existing.setAddress(request.getAddress());
        Restaurant updated = restaurantRepository.save(existing);
        return convertToResponse(updated);
    }

    public RestaurantResponse convertToResponse(Restaurant restaurant) {
        return new RestaurantResponse(
                restaurant.getId(),
                restaurant.getName(),
                restaurant.getAddress(),
                restaurant.getRating(),
                restaurant.getOwner().getId()
        );
    }
    
    public Restaurant getRestaurantById(Long id) {
        return restaurantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Restaurant not found with ID: " + id));
    }
}