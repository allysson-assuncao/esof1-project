package org.example.backend.dto.Order;

public record OrderRequestDTO(
                              Long parentOrderId,
                              Long guestTabId,
                              OrderItemDTO[] items,
                              String waiterEmail) {
}
