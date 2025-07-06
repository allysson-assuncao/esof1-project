package org.example.backend.dto.Order;

import lombok.Builder;
import org.example.backend.model.enums.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
public record FilteredOrderKanbanDTO(Long id, String productName, int amount, String observation, LocalDateTime orderedTime, OrderStatus status, String workstationName, List<FilteredOrderKanbanDTO> additionalOrders) { }
