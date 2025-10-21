package com.ey.foodEase.response.dto;

import java.math.BigDecimal;

public class MenuItemResponse {
	private Long id;
	private String name;
	private String description;
	private BigDecimal price;
	private String category;
	private Boolean availability;
	private Long restaurantId;
	private Boolean isVeg;

	public MenuItemResponse(Long id, String name, String description, BigDecimal price, String category,
			Boolean availability, Long restaurantId, Boolean isVeg) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.price = price;
		this.category = category;
		this.availability = availability;
		this.restaurantId = restaurantId;
		this.isVeg = isVeg;
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public String getCategory() {
		return category;
	}

	public Boolean getAvailability() {
		return availability;
	}

	public Long getRestaurantId() {
		return restaurantId;
	}

	public Boolean getIsVeg() {
		return isVeg;
	}
}