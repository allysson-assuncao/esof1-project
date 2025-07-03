package org.example.backend.dto.Order;

import org.example.backend.model.enums.OrderStatus;

import java.time.LocalDateTime;

public record FlatOrderDTO(
        Long id,
        int amount,
        String observation,
        OrderStatus status,
        LocalDateTime orderedTime,
        Long parentOrderId,
        Long guestTabId,
        String productName,
        double productUnitPrice,
        String waiterName
) {
    public FlatOrderDTO(Long id, int amount, String observation, OrderStatus status, LocalDateTime orderedTime, Long parentOrderId, Long guestTabId, String productName, double productUnitPrice, String waiterName) {
        this.id = id;
        this.amount = amount;
        this.observation = observation;
        this.status = status;
        this.orderedTime = orderedTime;
        this.parentOrderId = parentOrderId;
        this.guestTabId = guestTabId;
        this.productName = productName;
        this.productUnitPrice = productUnitPrice;
        this.waiterName = waiterName;
    }
}
