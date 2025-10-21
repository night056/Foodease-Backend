package com.ey.foodEase.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ey.foodEase.model.Delivery;
import com.ey.foodEase.model.DeliveryStatus;
import com.ey.foodEase.model.User;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {

	List<Delivery> findByDeliveryPartnerAndStatus(User deliveryPartner, DeliveryStatus status);

	List<Delivery> findByDeliveryPartner(User deliveryPartner);

	List<Delivery> findByStatus(DeliveryStatus status);
	
	Optional<Delivery> findTopByOrderIdOrderByLastUpdateDesc(Long orderId);
}
