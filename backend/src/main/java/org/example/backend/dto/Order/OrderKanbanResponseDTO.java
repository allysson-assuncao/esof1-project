package org.example.backend.dto.Order;

import org.example.backend.dto.FilteredPageDTO;
import org.example.backend.model.enums.OrderStatus;

import java.util.Map;

public record OrderKanbanResponseDTO(Map<OrderStatus, FilteredPageDTO<FilteredOrderKanbanDTO>> columns) {}
