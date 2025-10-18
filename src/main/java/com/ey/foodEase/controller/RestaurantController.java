package com.ey.foodEase.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ey.foodEase.model.Restaurant;
import com.ey.foodEase.request.RestaurantRequest;
import com.ey.foodEase.request.RestaurantResponse;
import com.ey.foodEase.service.RestaurantService;

@RestController
@RequestMapping("/api/restaurants")
public class RestaurantController {

    @Autowired
    private RestaurantService restaurantService;

    @PostMapping
    public RestaurantResponse addRestaurant(@RequestBody RestaurantRequest request) {
        return restaurantService.addRestaurant(request);
    }

    @GetMapping
    public List<RestaurantResponse> getAllRestaurants() {
        return restaurantService.getAllRestaurants();
    }

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
}