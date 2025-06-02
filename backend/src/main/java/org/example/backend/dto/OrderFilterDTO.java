package org.example.backend.dto;

import lombok.Builder;
import org.example.backend.model.enums.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
public record OrderFilterDTO(List<UUID> guestTabIds, List<OrderStatus> status, LocalDateTime orderedTime, List<UUID> waiterIds, String productName) { }
