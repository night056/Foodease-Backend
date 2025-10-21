package com.ey.foodEase.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ey.foodEase.model.Delivery;
import com.ey.foodEase.model.DeliveryStatus;
import com.ey.foodEase.model.User;
import com.ey.foodEase.repository.DeliveryRepository;
import com.ey.foodEase.repository.UserRepository;
import com.ey.foodEase.response.dto.DeliveryResponse;

import jakarta.transaction.Transactional;

@Service
public class DeliveryService {

	@Autowired
	private DeliveryRepository deliveryRepo;

	@Autowired
	private UserRepository userRepo;

	public List<DeliveryResponse> getAssignedDeliveries(Long userId) {
		User user = userRepo.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

		return deliveryRepo.findByDeliveryPartnerAndStatus(user, DeliveryStatus.ASSIGNED).stream()
				.map(this::mapToResponse).collect(Collectors.toList());
	}

	public List<DeliveryResponse> getDeliveryHistory(Long userId) {
		User user = userRepo.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

		return deliveryRepo.findByDeliveryPartner(user).stream().map(this::mapToResponse).collect(Collectors.toList());
	}

	public void updateDeliveryStatus(Long deliveryId, DeliveryStatus status, Long userId) {
		Delivery delivery = deliveryRepo.findById(deliveryId)
				.orElseThrow(() -> new RuntimeException("Delivery not found"));

		if (!Long.valueOf(delivery.getDeliveryPartner().getId()).equals(userId)) {
			throw new RuntimeException("Unauthorized access");
		}

		delivery.setStatus(status);
		delivery.setLastUpdate(LocalDateTime.now());

		if (status == DeliveryStatus.DELIVERED) {
			delivery.setDeliveredAt(LocalDateTime.now());
		}

		deliveryRepo.save(delivery);
	}

	public void makeDeliveryAvailable(Long orderId) {
		Delivery delivery = new Delivery();
		delivery.setOrderId(orderId);
		delivery.setStatus(DeliveryStatus.AVAILABLE);
		delivery.setAssignedAt(LocalDateTime.now());
		delivery.setLastUpdate(LocalDateTime.now());
		deliveryRepo.save(delivery);
	}

	@Transactional
	public boolean acceptDelivery(Long deliveryId, User deliveryPartner) {
		Delivery delivery = deliveryRepo.findById(deliveryId)
			.orElseThrow(() -> new RuntimeException("Delivery not found with ID: " + deliveryId));
		if (delivery.getStatus() != DeliveryStatus.AVAILABLE) {
			return false; 
		}

		delivery.setDeliveryPartner(deliveryPartner);
		delivery.setStatus(DeliveryStatus.ASSIGNED);
		delivery.setLastUpdate(LocalDateTime.now());
		deliveryRepo.save(delivery);
		return true;
	}
	
	public DeliveryResponse trackDeliveryByOrderId(Long orderId) {
	    Delivery delivery = deliveryRepo.findTopByOrderIdOrderByLastUpdateDesc(orderId)
	        .orElseThrow(() -> new RuntimeException("No delivery found for order ID: " + orderId));

	    return mapToResponse(delivery);
	}

	public DeliveryResponse mapToResponse(Delivery delivery) {
	    return new DeliveryResponse(
	        delivery.getId(),
	        delivery.getOrderId(),
	        delivery.getDeliveryPartner().getUsername(),
	        delivery.getStatus(),
	        delivery.getAssignedAt(),
	        delivery.getDeliveredAt(),
	        delivery.getLastUpdate()
	    );
	}
}