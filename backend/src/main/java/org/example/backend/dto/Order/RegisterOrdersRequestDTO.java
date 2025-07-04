package org.example.backend.dto.Order;

import java.util.List;

public record RegisterOrdersRequestDTO(Long guestTabId, Long parentOrderId, List<OrderItemDTO> orderItems, String waiterEmail) { }
