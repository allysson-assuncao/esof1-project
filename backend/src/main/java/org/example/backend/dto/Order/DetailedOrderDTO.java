package org.example.backend.dto.Order;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record DetailedOrderDTO(Long id,
                               int tableNumber,
                               String guestName,
                               String waiterName,
                               String productName,
                               double price,
                               int amount,
                               LocalDateTime timeOrdered) {
}
