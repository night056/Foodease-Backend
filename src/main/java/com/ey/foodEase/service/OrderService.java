package com.ey.foodEase.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.ey.foodEase.model.MenuItem;
import com.ey.foodEase.model.Order;
import com.ey.foodEase.model.OrderItem;
import com.ey.foodEase.model.OrderStatus;
import com.ey.foodEase.model.Restaurant;
import com.ey.foodEase.repository.MenuItemRepository;
import com.ey.foodEase.repository.OrderItemRepository;
import com.ey.foodEase.repository.OrderRepository;
import com.ey.foodEase.repository.RestaurantRepository;
import com.ey.foodEase.request.OrderItemRequest;
import com.ey.foodEase.request.OrderRequest;
import com.ey.foodEase.response.OrderItemResponse;
import com.ey.foodEase.response.OrderResponse;
import com.ey.foodEase.util.JwtUtil;

@Service
public class OrderService {

	@Autowired
	private OrderRepository orderRepo;

	@Autowired
	private OrderItemRepository itemRepo;
	
	@Autowired
	private MenuItemRepository menuItemRepo;

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	RestaurantRepository restaurantRepo;

	public OrderResponse placeOrder(OrderRequest request) {
		Order order = new Order();
		order.setCustomerId(request.getCustomerId());
		order.setRestaurantId(request.getRestaurantId());
		order.setStatus(OrderStatus.PENDING);
		order.setDate(LocalDateTime.now());

		List<OrderItem> items = new ArrayList<>();
		BigDecimal total = BigDecimal.ZERO;

		for (OrderItemRequest dto : request.getItems()) {
			OrderItem item = new OrderItem();
			item.setMenuItemId(dto.getMenuItemId());
			item.setQuantity(dto.getQuantity());
			item.setPrice(fetchPrice(dto.getMenuItemId()));
			item.setOrder(order);

			total = total.add(item.getPrice().multiply(BigDecimal.valueOf(dto.getQuantity())));
			items.add(item);
		}

		order.setTotalAmt(total);
		order.setItems(items);
		orderRepo.save(order);

		return mapToResponse(order);
	}

	public OrderResponse createDraftOrder(OrderRequest request, String token) {
		Long userId = jwtUtil.extractUserId(token);
		if (!request.getCustomerId().equals(userId)) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN,
					"You are not allowed to create a draft for another user");
		}

		Order order = new Order();
		order.setCustomerId(userId);
		order.setRestaurantId(request.getRestaurantId());
		order.setStatus(OrderStatus.DRAFT);
		order.setDate(LocalDateTime.now());
		order.setItems(new ArrayList<>());
		order.setTotalAmt(BigDecimal.ZERO);
		orderRepo.save(order);
		return mapToResponse(order);
	}

	public void addItemToDraft(Long orderId, OrderItemRequest itemRequest, String token) {
		Order order = getDraftOrder(orderId, token);

		OrderItem item = new OrderItem();
		item.setOrder(order);
		item.setMenuItemId(itemRequest.getMenuItemId());
		item.setQuantity(itemRequest.getQuantity());
		item.setPrice(fetchPrice(itemRequest.getMenuItemId()));
		itemRepo.save(item);

		order.getItems().add(item);
		recalculateTotal(order);
		orderRepo.save(order);
	}

	public void updateItemQuantity(Long orderId, Long itemId, int quantity, String token) {
		Order order = getDraftOrder(orderId, token);

		OrderItem item = itemRepo.findById(itemId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found"));
		if (!item.getOrder().getId().equals(orderId)) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Item does not belong to order");
		}

		item.setQuantity(quantity);
		itemRepo.save(item);
		recalculateTotal(order);
		orderRepo.save(order);
	}

	public void removeItemFromDraft(Long orderId, Long itemId, String token) {
		Order order = getDraftOrder(orderId, token);

		OrderItem item = itemRepo.findById(itemId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found"));
		if (!item.getOrder().getId().equals(orderId)) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Item does not belong to order");
		}

		itemRepo.delete(item);
		order.getItems().removeIf(i -> i.getId().equals(itemId));
		recalculateTotal(order);
		orderRepo.save(order);
	}

	public OrderResponse placeDraftOrder(Long orderId, String token) {
		Order order = getDraftOrder(orderId, token);
		order.setStatus(OrderStatus.PENDING);
		order.setDate(LocalDateTime.now());
		orderRepo.save(order);
		return mapToResponse(order);
	}

	public List<OrderResponse> getOrdersByCustomer(Long customerId, String token) {
		Long userId = jwtUtil.extractUserId(token);
		if (!customerId.equals(userId)) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN,
					"You are not allowed to view another customer's orders");
		}

		return orderRepo.findByCustomerId(customerId).stream().map(this::mapToResponse).collect(Collectors.toList());
	}

	public List<OrderResponse> getOrdersByRestaurant(Long restaurantId) {
		return orderRepo.findByRestaurantId(restaurantId).stream().map(this::mapToResponse)
				.collect(Collectors.toList());
	}

	public void updateOrderStatus(Long orderId, OrderStatus status) {
		Order order = orderRepo.findById(orderId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
		order.setStatus(status);
		orderRepo.save(order);
	}

	public void cancelOrder(Long orderId, String token) {
		Long userId = jwtUtil.extractUserId(token);
		Order order = orderRepo.findById(orderId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

		if (!order.getCustomerId().equals(userId)) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to cancel this order");
		}

		if (order.getStatus() == OrderStatus.CONFIRMED || order.getStatus() == OrderStatus.DELIVERED) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"Cannot cancel an order that is already confirmed or delivered");
		}

		order.setStatus(OrderStatus.CANCELLED);
		orderRepo.save(order);
	}

	private Order getDraftOrder(Long orderId, String token) {
		Long userId = jwtUtil.extractUserId(token);
		Order order = orderRepo.findById(orderId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

		if (!order.getCustomerId().equals(userId)) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to modify this order");
		}

		if (order.getStatus() != OrderStatus.DRAFT) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot modify non-draft order");
		}

		return order;
	}

	private void recalculateTotal(Order order) {
		BigDecimal total = order.getItems().stream()
				.map(i -> i.getPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
				.reduce(BigDecimal.ZERO, BigDecimal::add);
		order.setTotalAmt(total);
	}

	private BigDecimal fetchPrice(Long menuItemId) {
	    return menuItemRepo.findById(menuItemId)
	            .map(MenuItem::getPrice)
	            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Menu item not found"));
	}

	private String fetchMenuItemName(Long menuItemId) {
	    return menuItemRepo.findById(menuItemId)
	            .map(MenuItem::getName)
	            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Menu item not found"));
	}

	private OrderResponse mapToResponse(Order order) {
		List<OrderItemResponse> itemDTOs = order.getItems().stream().map(i -> {
			OrderItemResponse dto = new OrderItemResponse();
			dto.setMenuItemId(i.getMenuItemId());
			dto.setQuantity(i.getQuantity());
			dto.setPrice(i.getPrice());
			dto.setName(fetchMenuItemName(i.getMenuItemId()));
			return dto;
		}).collect(Collectors.toList());

		OrderResponse response = new OrderResponse();
		response.setOrderId(order.getId());
		response.setStatus(order.getStatus());
		response.setTotalAmt(order.getTotalAmt());
		response.setItems(itemDTOs);
		return response;
	}

	public OrderResponse getCurrentOrder(Long customerId, String token) {
		Long userId = jwtUtil.extractUserId(token);
		if (!customerId.equals(userId)) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN,
					"You are not allowed to view another customer's order");
		}

		return orderRepo.findByCustomerId(customerId).stream()
				.filter(order -> order.getStatus() != OrderStatus.CANCELLED
						&& order.getStatus() != OrderStatus.DELIVERED)
				.sorted(Comparator.comparing(Order::getDate).reversed()).findFirst().map(this::mapToResponse)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No current order found"));
	}

	public void approveOrder(Long orderId, String token) {
		Long ownerId = jwtUtil.extractUserId(token);

		Order order = orderRepo.findById(orderId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

		Restaurant restaurant = restaurantRepo.findById(order.getRestaurantId())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Restaurant not found"));

		System.out.println("Approving Order:");
		System.out.println("Order ID: " + orderId);
		System.out.println("Restaurant ID: " + restaurant.getId());
		System.out.println("Owner ID from token: " + ownerId);
		System.out.println("Owner ID from code: " + restaurant.getOwner());

		if (!Long.valueOf(restaurant.getOwner().getId()).equals(ownerId)) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to approve this order");
		}

		if (order.getStatus() != OrderStatus.PENDING) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only pending orders can be approved");
		}

		order.setStatus(OrderStatus.CONFIRMED);
		orderRepo.save(order);
	}

	public List<OrderResponse> getPendingOrdersForOwner(Long ownerId) {

		List<Restaurant> restaurants = restaurantRepo.findByOwnerId(ownerId);

		List<Long> restaurantIds = restaurants.stream().map(Restaurant::getId).collect(Collectors.toList());

		List<Order> pendingOrders = orderRepo.findByRestaurantIdInAndStatus(restaurantIds, OrderStatus.PENDING);

		return pendingOrders.stream().map(this::mapToResponse).collect(Collectors.toList());
	}
	
	

}