package com.ey.foodEase.controller;

import com.ey.foodEase.request.dto.MenuItemRequest;
import com.ey.foodEase.response.dto.MenuItemResponse;
import com.ey.foodEase.service.MenuItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/menu-items")
public class MenuItemController {

    @Autowired
    private MenuItemService menuItemService;

    @PreAuthorize("hasRole('OWNER')")
    @PostMapping
    public MenuItemResponse addMenuItem(@RequestBody MenuItemRequest request,
                                        @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        return menuItemService.addMenuItem(request, token);
    }

    @GetMapping("/restaurant/{restaurantId}")
    public List<MenuItemResponse> getMenuItemsByRestaurant(@PathVariable Integer restaurantId) {
        return menuItemService.getMenuItemsByRestaurantId(restaurantId);
    }

    @PreAuthorize("hasRole('OWNER')")
    @PutMapping("/{id}/availability")
    public MenuItemResponse updateAvailability(@PathVariable Long id,
                                               @RequestParam Boolean available) {
        return menuItemService.updateAvailability(id, available);
    }

    @PreAuthorize("hasRole('OWNER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMenuItem(@PathVariable Long id) {
        menuItemService.deleteMenuItem(id);
        return ResponseEntity.noContent().build();
    }
}