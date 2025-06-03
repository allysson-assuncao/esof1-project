package org.example.backend.dto;

import lombok.Builder;
import org.example.backend.model.enums.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record OrderDTO(Long id, int amount, String observation, OrderStatus status, LocalDateTime orderedTime, List<Long> additionalOrders, Long guestTabId, String productName, double price, String waiterName) { }
