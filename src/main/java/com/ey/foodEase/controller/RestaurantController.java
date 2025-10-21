package com.ey.foodEase.controller;

import com.ey.foodEase.model.Restaurant;
import com.ey.foodEase.request.dto.RestaurantRequest;
import com.ey.foodEase.request.dto.RestaurantResponse;
import com.ey.foodEase.request.dto.RatingRequest;
import com.ey.foodEase.service.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/restaurants")
public class RestaurantController {

    @Autowired
    private RestaurantService restaurantService;

    @PreAuthorize("hasRole('OWNER')")
    @PostMapping
    public RestaurantResponse addRestaurant(@RequestBody RestaurantRequest request) {
        return restaurantService.addRestaurant(request);
    }

    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    @GetMapping
    public List<RestaurantResponse> getAllRestaurants() {
        return restaurantService.getAllRestaurants();
    }

    @PreAuthorize("hasRole('OWNER')")
    @GetMapping("/owner/{ownerId}")
    public List<RestaurantResponse> getRestaurantsByOwner(@PathVariable Long ownerId) {
        return restaurantService.getRestaurantsByOwnerId(ownerId);
    }

    @PutMapping("/{id}")
    public RestaurantResponse updateRestaurant(@PathVariable Integer id, @RequestBody RestaurantRequest request) {
        return restaurantService.updateRestaurant(id, request);
    }

    @GetMapping("/{id}")
    public RestaurantResponse getRestaurantById(@PathVariable Long id) {
        Restaurant restaurant = restaurantService.getRestaurantById(id);
        return restaurantService.convertToResponse(restaurant);
    }

    
    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping("/{restaurantId}/rate")
    public String rateRestaurant(@PathVariable Long restaurantId,
                                 @RequestBody RatingRequest ratingDto,
                                 Authentication authentication) {
        String username = authentication.getName();
        restaurantService.addRating(restaurantId, ratingDto.getScore(), username);
        return "Rating submitted successfully";
    }
}