package com.ey.foodEase.response.dto;

import java.time.LocalDateTime;

import com.ey.foodEase.model.DeliveryStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryResponse {
    private Long deliveryId;
    private Long orderId;
    private String deliveryPartnerName;
    private DeliveryStatus status;
    private LocalDateTime assignedAt;
    private LocalDateTime deliveredAt;
    private LocalDateTime lastUpdate;
}