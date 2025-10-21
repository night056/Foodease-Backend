package com.ey.foodEase.service;

import com.ey.foodEase.model.MenuItem;
import com.ey.foodEase.model.Restaurant;
import com.ey.foodEase.repository.MenuItemRepository;
import com.ey.foodEase.repository.RestaurantRepository;
import com.ey.foodEase.request.dto.MenuItemRequest;
import com.ey.foodEase.response.dto.MenuItemResponse;
import com.ey.foodEase.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MenuItemService {

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private JwtUtil jwtUtil;

    public MenuItemResponse addMenuItem(MenuItemRequest request, String token) {
        Long loggedInUserId = jwtUtil.extractUserId(token);

        Restaurant restaurant = restaurantRepository.findById(request.getRestaurantId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Restaurant not found"));

        if (!Long.valueOf(restaurant.getOwner().getId()).equals(loggedInUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not the owner of this restaurant");
        }

        MenuItem item = new MenuItem();
        item.setName(request.getName());
        item.setDesc(request.getDescription());
        item.setPrice(request.getPrice());
        item.setCategory(request.getCategory());
        item.setAvailability(request.getAvailability());
        item.setIsVeg(request.getIsVeg());
        item.setRestaurant(restaurant);

        MenuItem saved = menuItemRepository.save(item);
        return convertToResponse(saved);
    }

    public List<MenuItemResponse> getMenuItemsByRestaurantId(Integer restaurantId) {
        return menuItemRepository.findByRestaurantId(restaurantId)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public MenuItemResponse updateAvailability(Long id, Boolean availability) {
        MenuItem item = menuItemRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Menu item not found"));
        item.setAvailability(availability);
        return convertToResponse(menuItemRepository.save(item));
    }

    public void deleteMenuItem(Long id) {
        menuItemRepository.deleteById(id);
    }

    private MenuItemResponse convertToResponse(MenuItem item) {
        return new MenuItemResponse(
                item.getId(),
                item.getName(),
                item.getDesc(),
                item.getPrice(),
                item.getCategory(),
                item.getAvailability(),
                item.getRestaurant().getId(),
                item.getIsVeg()
        );
    }
}