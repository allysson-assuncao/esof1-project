package org.example.backend.dto.Order;

import java.util.UUID;

public record OrderRequestDTO(
                              Long parentOrderId,
                              Long guestTabId,
                              OrderItemDTO[] items,
                              String userEmail) {
}
