package com.ey.foodEase.request.dto;

import com.ey.foodEase.model.DeliveryStatus;
import lombok.Data;

@Data
public class DeliveryRequest {
    private Long deliveryId;
    private DeliveryStatus status;
}