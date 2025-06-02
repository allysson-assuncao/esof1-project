package org.example.backend.dto;

import lombok.Builder;
import org.example.backend.model.enums.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
public record OrderDTO(UUID id, int amount, String observation, OrderStatus status, LocalDateTime orderedTime, List<UUID> additionalOrders, UUID guestTabId, String productName, double price, String waiterName) { }
