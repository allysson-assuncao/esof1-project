package org.example.backend.dto;

import lombok.Builder;
import org.example.backend.model.enums.OrderStatus;

import java.time.LocalDateTime;
import java.util.Set;

@Builder
public record OrderDTO(Long id, int amount, OrderStatus status, String observation, LocalDateTime orderedTime, Set<Long> additionalOrders, String productName, double productUnitPrice, String waiterName) { }
